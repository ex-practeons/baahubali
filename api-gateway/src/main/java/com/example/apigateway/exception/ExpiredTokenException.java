package com.example.apigateway.exception;

public class ExpiredTokenException extends GatewayAuthenticationException {

    public ExpiredTokenException(Throwable cause) {
        super(AuthErrorCode.TOKEN_EXPIRED, "JWT has expired", cause);
    }
}