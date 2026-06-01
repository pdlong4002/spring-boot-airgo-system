package com.ramennsama.springboot.authservice.config;

import com.ramennsama.springboot.authservice.enums.Role;
import com.ramennsama.springboot.authservice.filter.JwtFilter;
import com.ramennsama.springboot.authservice.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.ramennsama.springboot.authservice.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.ramennsama.springboot.authservice.oauth2.repository.HttpCookieOAuthorizationRequestRepository;
import com.ramennsama.springboot.authservice.oauth2.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/oauth2/redirect",
            "/oauth2/authorize/**",
            "/oauth2/callback/**",
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh-token",
            "/api/v1/auth/oauth2-success",
            "/api/v1/auth/oauth2-failure",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/swagger-resources/**",
            "/webjars/**",
            "/docs/**",
            "/error"
    };

    @Value("${app.cors.allowedOrigins:http://localhost:3000}")
    private String allowedOrigins;

    private final JwtFilter jwtFilter;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final HttpCookieOAuthorizationRequestRepository httpCookieOAuthorizationRequestRepository;

    private final OAuth2AuthenticationSuccessHandler successHandler;

    private final OAuth2AuthenticationFailureHandler failureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers("/api/v1/accounts")
                        .hasAnyRole(Role.ADMIN.name(), Role.MANAGER.name())
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint()))
                .oauth2Login(oauth2 -> oauth2

                        .authorizationEndpoint(authEndpoint -> authEndpoint
                                .baseUri("/oauth2/authorize")
                                .authorizationRequestRepository(httpCookieOAuthorizationRequestRepository)
                        )

                        .redirectionEndpoint(redirEndpoint -> redirEndpoint
                                .baseUri("/oauth2/callback/*")
                        )

                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )

                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                );
        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");

            // Ngăn browser hiện popup Basic Auth
            response.setHeader("WWW-Authenticate", "");
            
            String message = authException.getMessage();
            if (message == null || message.equalsIgnoreCase("Full authentication is required to access this resource")) {
                message = "JWT expired or invalid";
            }

            response.getWriter().write(String.format("""
            {
                "status": 401,
                "error": "Unauthorized",
                "message": "%s"
            } """, message));
        };
    }

}
