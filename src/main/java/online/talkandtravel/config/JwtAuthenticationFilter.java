package online.talkandtravel.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.talkandtravel.exception.model.ExceptionResponse;
import online.talkandtravel.exception.token.AuthenticationHeaderIsInvalidException;
import online.talkandtravel.facade.AuthenticationFacade;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JwtAuthenticationFilter is a Spring security filter that intercepts incoming HTTP requests
 * and authenticates them using JWT tokens. It ensures that only requests with valid tokens are
 * processed further in the application.
 *
 * <p>This filter checks for the presence of an Authorization header, extracts the JWT token,
 * validates it, and authenticates the user if necessary.
 *
 * <p>If the token is invalid or missing, the filter will respond with an unauthorized error.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final TokenService tokenService;
  private final ObjectMapper objectMapper;
  private final AuthenticationFacade authFacade;

  /**
   * Processes each incoming HTTP request and attempts to authenticate it based on the JWT token
   * present in the Authorization header. If the token is valid, the request proceeds; otherwise,
   * an unauthorized error response is returned.
   *
   * @param request The incoming HTTP request to be filtered.
   * @param response The HTTP response object where any errors are sent.
   * @param filterChain The chain of filters that the request will pass through.
   *
   * @throws IOException If an input or output exception occurs.
   */
  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws IOException {
    try {
      authenticateRequest(request);
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      log.error("Exception in JwtAuthenticationFilter: {}", e.getMessage());
      sendErrorResponse(response);
    }
  }

  /**
   * Checks if the incoming request contains an Authorization header, and if so, extracts
   * and validates the JWT token. If the token is valid and the user is not already authenticated,
   * it proceeds to authenticate the user.
   *
   * @param request The HTTP request, which may be used during the authentication process.
   */
  private void authenticateRequest(HttpServletRequest request) {
    final String authHeader = request.getHeader("Authorization");
    if (authHeader == null) return;
    String token = getTokenFromAuthHeader(authHeader);
    tokenService.validateToken(token);

    if (!authFacade.isUserAuthenticated()) {
      authFacade.authenticateUser(token, request);
    }
  }

  /**
   * Extracts the JWT token from the Authorization header. The header must start with "Bearer ",
   * otherwise an {@link AuthenticationHeaderIsInvalidException} exception is thrown.
   *
   * @param authHeader The Authorization header containing the JWT token.
   * @return The JWT token as a string.
   *
   * @throws AuthenticationHeaderIsInvalidException If the header does not start with "Bearer ".
   */
  private String getTokenFromAuthHeader(String authHeader) {
    if (!authHeader.startsWith("Bearer ")) {
      throw new AuthenticationHeaderIsInvalidException(authHeader);
    }
    return authHeader.substring(7);
  }

  /**
   * Sends an error response to the client with an "Unauthorized" status code (401).
   * This method is called when authentication fails.
   *
   * @param response The HTTP response object where the error message is written.
   *
   * @throws IOException If an input or output exception occurs while writing the error response.
   */
  private void sendErrorResponse(HttpServletResponse response) throws IOException {
    ExceptionResponse exceptionResponse =
        new ExceptionResponse(
            "Authentication failed", HttpStatus.UNAUTHORIZED, ZonedDateTime.now());
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");
    response.getWriter().write(objectMapper.writeValueAsString(exceptionResponse));
  }
}
