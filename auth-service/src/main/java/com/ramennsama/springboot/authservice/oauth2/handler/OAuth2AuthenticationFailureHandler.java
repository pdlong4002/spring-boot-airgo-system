package com.ramennsama.springboot.authservice.oauth2.handler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        log.error("OAuth2 authentication failed: {}", exception.getMessage());

        // Redirect về Backend API để hiển thị lỗi dạng JSON thay vì trang trắng
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/api/v1/auth/oauth2-failure")
                .queryParam("error", URLEncoder.encode(exception.getLocalizedMessage(), StandardCharsets.UTF_8))
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);

//        logger.error("OAuth2 authentication failed", exception);
//        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!");
    }
}
