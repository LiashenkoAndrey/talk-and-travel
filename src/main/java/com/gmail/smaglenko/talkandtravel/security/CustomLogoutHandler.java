package com.gmail.smaglenko.talkandtravel.security;

import com.gmail.smaglenko.talkandtravel.model.Token;
import com.gmail.smaglenko.talkandtravel.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {
    private final TokenService tokenService;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (StringUtils.isEmpty(authHeader) || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        var token = tokenService.findByToken(jwt).orElse(null);
        if (token != null) {
            token.setRevoked(true);
            token.setExpired(true);
            tokenService.save(token);
        }
    }
}
