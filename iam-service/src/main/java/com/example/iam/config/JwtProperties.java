package com.example.iam.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        long expirationMs,
        String cookieName,
        String cookieDomain,
        boolean cookieSecure,
        String cookieSameSite
) {}
