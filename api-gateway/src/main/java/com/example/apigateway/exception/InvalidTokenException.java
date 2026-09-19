package com.example.apigateway.exception;

public class InvalidTokenException extends GatewayAuthenticationException {

    public InvalidTokenException(String reason) {
        super(AuthErrorCode.TOKEN_INVALID, reason, null);
    }

    public InvalidTokenException(String reason, Throwable cause) {
        super(AuthErrorCode.TOKEN_INVALID, reason, cause);
    }
}