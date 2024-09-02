package online.talkandtravel.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;
import online.talkandtravel.model.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
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
 * @param http {@link HttpSecurity} to configure security settings.
 * @return {@link SecurityFilterChain} configured with security settings.
 * @throws Exception if any configuration errors occur.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
  private static final String[] WHITE_LIST_URL = {
    "/api/authentication/**",
    "/swagger-ui/**",
    "/v3/**",
    "/api/users/exists-by-email/**",
    "/ws/**",
    "/privacy-policy",
    "/public-terms-of-service",
      "/api/avatars/user/{userID}",
      "api/v2/user/{userID}/avatar"
  };
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final AuthenticationProvider authenticationProvider;
  private final CorsConfigurationSource corsConfigurationSource;
  private final LogoutHandler logoutHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            req ->
                req.requestMatchers(WHITE_LIST_URL)
                    .permitAll()
                    .requestMatchers(
                        POST, "/api/countries/", "/api/avatars", "/api/group-messages/", "/api/user/avatar")
                    .hasAnyAuthority(Role.USER.name())
                    .requestMatchers(PUT, "/api/users/", "/api/countries/", "/api/participants/")
                    .hasAnyAuthority(Role.USER.name())
                    .requestMatchers(GET, "/api/users/", "/api/countries/", "/api/group-messages/")
                    .hasAnyAuthority(Role.USER.name())
                    .anyRequest()
                    .authenticated())
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(
            logout ->
                logout
                    .logoutUrl("/api/authentication/logout")
                    .addLogoutHandler(logoutHandler)
                    .logoutSuccessHandler(
                        (request, response, authentication) ->
                            SecurityContextHolder.clearContext()));
    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }
}
