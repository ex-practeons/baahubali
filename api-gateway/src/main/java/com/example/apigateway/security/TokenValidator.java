package com.example.apigateway.security;

import com.example.apigateway.exception.GatewayAuthenticationException;
import reactor.core.publisher.Mono;

public interface TokenValidator {
    /**
     * @param token raw token string
     * @return a Mono emitting the identity carried by the token
     * @throws GatewayAuthenticationException if the token is invalid or expired
     */
    Mono<AuthenticatedUser> validate(String token);
}