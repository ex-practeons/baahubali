package com.example.apigateway.security;

import com.example.apigateway.exception.GatewayAuthenticationException;

public interface TokenValidator {

    /**
     * @param token raw token string
     * @return the identity carried by the token
     * @throws GatewayAuthenticationException if the token is invalid or expired
     */
    AuthenticatedUser validate(String token);
}