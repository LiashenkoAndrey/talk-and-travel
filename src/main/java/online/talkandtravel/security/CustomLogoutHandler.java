package online.talkandtravel.security;

import static online.talkandtravel.util.constants.ApiPathConstants.USERS_ONLINE_STATUS_ENDPOINT;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.talkandtravel.exception.model.ExceptionResponse;
import online.talkandtravel.model.dto.user.OnlineStatusDto;
import online.talkandtravel.service.OnlineService;
import online.talkandtravel.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/**
 * Custom implementation of {@link LogoutHandler} to handle user logout operations.
 *
 * <p>This class manages the logout process by:
 *
 * <ul>
 *   <li>Extracting the JWT token from the "Authorization" header of the request.
 *   <li>Finding the token using the {@link TokenService}.
 *   <li>Marking the token as expired and revoked if it exists.
 *   <li>Clearing the security context to ensure the user is fully logged out.
 *   <li>Notifying all users that the user has gone offline.
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {
  private final ObjectMapper objectMapper;
  private final TokenService tokenService;
  private final OnlineService onlineService;
  private final SimpMessagingTemplate messagingTemplate;

  @Override
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    String token = extractTokenFromHeader(request, response);
    if (token == null) {
      return;
    }

    Long userId = validateAndExtractUserId(token, response);
    if (userId == null) {
      return;
    }

    revokeToken(token);
    clearSecurityContext();
    notifyAllUserIsOffline(userId);
  }

  /**
   * Extracts the JWT token from the Authorization header. Returns null if the header is missing or
   * invalid and writes an error response.
   */
  private String extractTokenFromHeader(HttpServletRequest request, HttpServletResponse response) {
    final String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      writeBadRequestResponse(response);
      return null;
    }
    return authHeader.substring(7); // Extract JWT token
  }

  /**
   * Validates the JWT token and extracts the user ID. Returns null if validation fails and writes
   * an error response.
   */
  private Long validateAndExtractUserId(String token, HttpServletResponse response) {
    Long userId;
    try {
      userId = tokenService.validateTokenAndGetUserId(token);
    } catch (Exception e) {
      log.error(e.getMessage());
      writeBadRequestResponse(response);
      return null;
    }
    return userId;
  }

  /** Marks the token as expired and revoked. */
  private void revokeToken(String token) {
    var tokenEntity = tokenService.findByToken(token).orElse(null);
    if (tokenEntity != null) {
      tokenEntity.setExpired(true);
      tokenEntity.setRevoked(true);
      tokenService.save(tokenEntity);
    }
  }

  /** Clears the security context, effectively logging the user out. */
  private void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  /** Notifies all users that the given user has gone offline. */
  private void notifyAllUserIsOffline(Long userId) {
    OnlineStatusDto statusDto = onlineService.updateUserOnlineStatus(userId, false);
    messagingTemplate.convertAndSend(USERS_ONLINE_STATUS_ENDPOINT, statusDto);
  }

  /** Writes a Bad Request response when the request is invalid. */
  private void writeBadRequestResponse(HttpServletResponse response) {
    try {
      ExceptionResponse exceptionResponse =
          new ExceptionResponse(
              "Authorization header is missing or invalid",
              HttpStatus.BAD_REQUEST,
              ZonedDateTime.now());
      response.setStatus(HttpStatus.BAD_REQUEST.value());
      response.setContentType("application/json");
      response.getWriter().write(objectMapper.writeValueAsString(exceptionResponse));
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}
