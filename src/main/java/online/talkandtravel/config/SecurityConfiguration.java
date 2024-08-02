package online.talkandtravel.config;

import lombok.RequiredArgsConstructor;
import online.talkandtravel.model.Role;
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

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private static final String[] WHITE_LIST_URL = {
            "/api/authentication/**",
            "/swagger-ui/**",
            "/v3/**",
            "/api/users/exists-by-email/**",
            "/ws/",
            "/ws/**",
            "/privacy-policy",
            "/public-terms-of-service"
    };
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CorsConfigurationSource corsConfigurationSource;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                    req.requestMatchers(WHITE_LIST_URL)
                        .permitAll()
                        .requestMatchers("/ws-endpoint/**").permitAll()
                        .requestMatchers(POST, "/api/countries/", "/api/avatars",
                            "/api/group-messages/")
                        .hasAnyAuthority(Role.USER.name())
                        .requestMatchers(PUT, "/api/users/", "/api/countries/",
                            "/api/participants/")
                        .hasAnyAuthority(Role.USER.name())
                        .requestMatchers(GET, "/api/users/", "/api/countries/",
                            "/api/group-messages/")
                        .hasAnyAuthority(Role.USER.name())
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/authentication/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler(
                                        (request, response, authentication)
                                                -> SecurityContextHolder.clearContext())
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
