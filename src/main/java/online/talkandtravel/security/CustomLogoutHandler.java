package online.talkandtravel.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.talkandtravel.exception.model.ExceptionResponse;
import online.talkandtravel.service.TokenService;
import org.springframework.http.HttpStatus;
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
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {
  private final ObjectMapper objectMapper;
  private final TokenService tokenService;

  @Override
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      try {
        // Set response status to 400 Bad Request
        writeErrorResponse(response);
      } catch (IOException e) {
        log.error(e.getMessage());
      }
      return;
    }
    jwt = authHeader.substring(7);
    var token = tokenService.findByToken(jwt).orElse(null);
    if (token != null) {
      token.setExpired(true);
      token.setRevoked(true);
      tokenService.save(token);
      SecurityContextHolder.clearContext();
    }
  }

  /**
   * Sends an error response to the client with an "Unauthorized" status code (401). This method is
   * called when authentication fails.
   *
   * @param response The HTTP response object where the error message is written.
   * @throws IOException If an input or output exception occurs while writing the error response.
   */
  private void writeErrorResponse(HttpServletResponse response) throws IOException {
    ExceptionResponse exceptionResponse =
        new ExceptionResponse(
            "Authorization header is missing or invalid",
            HttpStatus.BAD_REQUEST,
            ZonedDateTime.now());
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    response.setContentType("application/json");
    response.getWriter().write(objectMapper.writeValueAsString(exceptionResponse));
  }
}
