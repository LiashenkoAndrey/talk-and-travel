package online.talkandtravel.config;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuration class for application-specific settings including security and CORS.
 *
 * <p>This class provides Spring configuration for security settings such as authentication and
 * password encoding, as well as CORS (Cross-Origin Resource Sharing) policies to manage allowed
 * origins, methods, headers, and credentials. It is annotated with {@link
 * org.springframework.context.annotation.Configuration} to denote that it contains bean definitions
 * that will be processed by the Spring container.
 *
 * <p>Key components configured in this class include:
 *
 * <ul>
 *   <li><strong>Authentication Provider:</strong> Configures {@link
 *       org.springframework.security.authentication.dao.DaoAuthenticationProvider} to handle user
 *       authentication with the provided {@link
 *       org.springframework.security.core.userdetails.UserDetailsService} and {@link
 *       org.springframework.security.crypto.password.PasswordEncoder}.
 *   <li><strong>Password Encoder:</strong> Uses {@link
 *       org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder} for encoding passwords to
 *       ensure secure storage.
 *   <li><strong>CORS Configuration:</strong> Defines CORS settings to control which origins,
 *       methods, headers, and credentials are allowed for cross-origin requests, using {@link
 *       org.springframework.web.cors.CorsConfigurationSource}.
 * </ul>
 *
 * <p>Constants:
 *
 * <ul>
 *   <li><strong>ALLOWED_ORIGINS:</strong> List of allowed origins for cross-origin requests.
 *   <li><strong>ALLOWED_METHODS:</strong> List of HTTP methods allowed in CORS requests.
 *   <li><strong>ALLOWED_HEADERS:</strong> List of headers allowed in CORS requests.
 *   <li><strong>EXPOSED_HEADERS:</strong> List of headers that are exposed in CORS responses.
 * </ul>
 *
 * <p>Methods:
 *
 * <ul>
 *   <li>{@link #authenticationProvider()} Configures the authentication provider for the
 *       application.
 *   <li>{@link #passwordEncoder()} Provides a bean for encoding passwords.
 *   <li>{@link #corsConfigurationSource()} Configures and returns a source for CORS settings.
 *   <li>{@link #createNewCorsConfiguration()} Creates a new CORS configuration with the specified
 *       settings.
 * </ul>
 *
 * @see org.springframework.security.authentication.AuthenticationProvider
 * @see org.springframework.security.crypto.password.PasswordEncoder
 * @see org.springframework.web.cors.CorsConfigurationSource
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

  @Value("${ALLOWED_ORIGINS}")
  private String ALLOWED_ORIGINS;

  private final List<String> ALLOWED_METHODS =
      Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD");
  private final List<String> ALLOWED_HEADERS =
      Arrays.asList("X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization");
  private final List<String> EXPOSED_HEADERS =
      Arrays.asList(
          "Content-Type", "Cache-Control", "Content-Language", "Content-Length", "Last-Modified");
  private final UserDetailsService userDetailsService;

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    return authenticationProvider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cors = createNewCorsConfiguration();
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cors);
    return source;
  }

  private CorsConfiguration createNewCorsConfiguration() {
    CorsConfiguration cors = new CorsConfiguration();
    cors.setAllowedOrigins(Arrays.asList(ALLOWED_ORIGINS.split(",")));
    cors.setAllowedMethods(ALLOWED_METHODS);
    cors.setAllowedHeaders(ALLOWED_HEADERS);
    cors.setExposedHeaders(EXPOSED_HEADERS);
    cors.setAllowCredentials(true);
    return cors;
  }
}
