package online.talkandtravel.config;

import static online.talkandtravel.util.constants.ApiPathConstants.LOGOUT_URL;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import online.talkandtravel.security.CustomLogoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Security configuration class for setting up Spring Security in the application.
 *
 * <p>This class is responsible for configuring security settings, including CORS, CSRF,
 * authorization, and session management. It also integrates JWT-based authentication and custom
 * logout handling.
 *
 * <p>Key aspects of the configuration include:
 *
 * <ul>
 *   <li><strong>CORS Configuration:</strong> Configures CORS using a {@link
 *       CorsConfigurationSource} to allow specific origins, methods, and headers.
 *   <li><strong>CSRF:</strong> Disabled to support stateless authentication.
 *   <li><strong>Authorization:</strong> Configures URL-based authorization:
 *       <ul>
 *         <li>Public endpoints (e.g., authentication, Swagger UI, privacy policy) are whitelisted
 *             and require no authentication.
 *         <li>POST, PUT, and GET requests to specific API endpoints require the "USER" role.
 *         <li>All other requests require authentication.
 *       </ul>
 *   <li><strong>Session Management:</strong> Configured to be stateless to support token-based
 *       authentication.
 *   <li><strong>Authentication Provider:</strong> Configured to use a custom {@link
 *       AuthenticationProvider}.
 *   <li><strong>JWT Filter:</strong> Adds a {@link JwtAuthenticationFilter} before the default
 *       {@link UsernamePasswordAuthenticationFilter} to handle JWT-based authentication.
 *   <li><strong>Logout Handling:</strong> Configures logout URL, adds a custom {@link
 *       LogoutHandler}, and clears the security context on logout success.
 * </ul>
 *
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private static final String[] WHITE_LIST_URL = {
    "/health",
    "/api/authentication/login",
    "/api/authentication/register",
    "/api/authentication/social/register",
    "/api/authentication/password-recovery",
    "/api/authentication/registration-confirmation",
    "/api/authentication/social/login",
    "/swagger-ui/**",
    "/v3/**",
    "/api/users/exists-by-email/**",
    "/ws/**",
    "/privacy-policy",
    "/public-terms-of-service",
    "/api/avatars/user/{userID}",
    "/api/v2/user/{userID}/avatar"
  };

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final AuthenticationProvider authenticationProvider;
  private final CorsConfigurationSource corsConfigurationSource;
  private final CustomLogoutHandler customLogoutHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            req -> req.requestMatchers(WHITE_LIST_URL).permitAll().anyRequest().authenticated())
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(
            logout ->
                logout
                    .logoutUrl(LOGOUT_URL)
                    .addLogoutHandler(customLogoutHandler)
                    .logoutSuccessHandler(getLogoutSuccessHandler())
                    .permitAll());
    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  private LogoutSuccessHandler getLogoutSuccessHandler() {
    return (request, response, authentication) -> {
      if (response.getStatus() != 400) {
        response.setStatus(HttpServletResponse.SC_OK);
      }
    };
  }
}
