package com.example.apigateway.security;

import java.util.Optional;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.apigateway.config.GatewaySecurityProperties;

@Component
public class CookieTokenExtractor implements TokenExtractor {

    private final String cookieName;

    public CookieTokenExtractor(GatewaySecurityProperties properties) {
        this.cookieName = properties.jwt().cookieName();
    }

    @Override
    public Optional<String> extract(ServerHttpRequest request) {
        return Optional.ofNullable(request.getCookies().getFirst(cookieName))
                .map(HttpCookie::getValue)
                .filter(StringUtils::hasText);
    }
}