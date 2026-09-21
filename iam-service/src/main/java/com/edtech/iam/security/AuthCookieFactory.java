package com.edtech.iam.security;

import com.edtech.iam.config.JwtProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieFactory {

    private final JwtProperties jwtProperties;

    public AuthCookieFactory(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public ResponseCookie buildAuthCookie(String token) {
        ResponseCookie.ResponseCookieBuilder builder = baseBuilder(token)
                .maxAge(jwtProperties.expirationMs() / 1000);

        return builder.build();
    }

    public ResponseCookie buildExpiredAuthCookie() {
        return baseBuilder("").maxAge(0).build();
    }

    private ResponseCookie.ResponseCookieBuilder baseBuilder(String value) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(jwtProperties.cookieName(), value)
                .httpOnly(true)
                .secure(jwtProperties.cookieSecure())
                .path("/")
                .sameSite(jwtProperties.cookieSameSite());

        if (jwtProperties.cookieDomain() != null && !jwtProperties.cookieDomain().isBlank()) {
            builder.domain(jwtProperties.cookieDomain());
        }

        return builder;
    }
}
