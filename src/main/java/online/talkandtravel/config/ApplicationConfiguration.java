package online.talkandtravel.config;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {
    private final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:3001",
            "http://localhost:3000",
            "http://localhost:5500",
            "http://localhost:8080",
            "http://localhost:63342",
            "https://reginavarybrus.github.io"
    );
    private final List<String> ALLOWED_METHODS = Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
    );
    private final List<String> ALLOWED_HEADERS = Arrays.asList(
            "X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"
    );
    private final List<String> EXPOSED_HEADERS = Arrays.asList(
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
        cors.setAllowedOrigins(ALLOWED_ORIGINS);
        cors.setAllowedMethods(ALLOWED_METHODS);
        cors.setAllowedHeaders(ALLOWED_HEADERS);
        cors.setExposedHeaders(EXPOSED_HEADERS);
        cors.setAllowCredentials(true);
        return cors;
    }
}
