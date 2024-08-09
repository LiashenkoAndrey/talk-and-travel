package online.talkandtravel.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import online.talkandtravel.service.TokenService;
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
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {
  private final TokenService tokenService;

  @Override
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
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
}
