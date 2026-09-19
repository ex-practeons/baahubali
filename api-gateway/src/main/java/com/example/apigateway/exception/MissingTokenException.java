package com.example.apigateway.exception;

public class MissingTokenException extends GatewayAuthenticationException {

    public MissingTokenException() {
        super(AuthErrorCode.TOKEN_MISSING, "Authentication cookie is absent or blank", null);
    }
}