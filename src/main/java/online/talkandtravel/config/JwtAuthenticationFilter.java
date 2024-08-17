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
import online.talkandtravel.service.JwtService;
import online.talkandtravel.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.thymeleaf.util.StringUtils;

/**
 * Filter to handle JWT (JSON Web Token) authentication for incoming HTTP requests.
 *
 * <p>This filter intercepts requests to validate JWT tokens and authenticate users based on the
 * token's validity. It extends {@link org.springframework.web.filter.OncePerRequestFilter} to
 * ensure that the filter is executed once per request.
 *
 * <p>Key Components:
 *
 * <ul>
 *   <li><strong>JwtService:</strong> Service for handling JWT operations such as extraction and
 *       validation.
 *   <li><strong>UserDetailsService:</strong> Service to load user details based on the username
 *       extracted from the JWT.
 *   <li><strong>TokenService:</strong> Service for managing and validating tokens stored in the
 *       database.
 * </ul>
 *
 * <p>Methods:
 *
 * <ul>
 *   <li>{@link #doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)} Handles the
 *       actual filtering of requests. It checks for the presence and validity of the JWT in the
 *       "Authorization" header, validates the token, and sets the authentication context if the
 *       token is valid.
 * </ul>
 *
 * <p>Process:
 *
 * <ol>
 *   <li>Checks for the "Authorization" header and verifies if it starts with "Bearer ". If not, the
 *       request is forwarded to the next filter.
 *   <li>Extracts the JWT from the header and retrieves the username associated with the token.
 *   <li>Loads the user details using the extracted username.
 *   <li>Validates the JWT and checks if the token is valid (not revoked or expired) using the
 *       {@link TokenService}.
 *   <li>If the token is valid, sets the authentication context with a {@link
 *       UsernamePasswordAuthenticationToken}.
 *   <li>Forwards the request to the next filter in the chain.
 * </ol>
 *
 * @see JwtService
 * @see UserDetailsService
 * @see TokenService
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenService tokenService;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws IOException {
    try {
      final String authHeader = request.getHeader("Authorization");
      final String jwt;
      final String userEmail;
      if (StringUtils.isEmpty(authHeader) || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
      }
      jwt = authHeader.substring(7);
      userEmail = jwtService.extractUsername(jwt);
      if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        var isTokenValid =
            tokenService.findByToken(jwt).map(t -> !t.isRevoked() && !t.isExpired()).orElse(false);
        if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      log.error("Exception in JwtAuthenticationFilter: {}", e.getMessage());
      sendErrorResponse(response);
    }
  }

  private void sendErrorResponse(HttpServletResponse response) throws IOException {
    ExceptionResponse exceptionResponse =
        new ExceptionResponse(
            "Authentication failed", HttpStatus.UNAUTHORIZED, ZonedDateTime.now());
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");
    response.getWriter().write(objectMapper.writeValueAsString(exceptionResponse));
  }
}
