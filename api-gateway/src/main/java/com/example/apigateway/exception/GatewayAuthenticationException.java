package com.example.apigateway.exception;

public abstract class GatewayAuthenticationException extends RuntimeException {

    private final AuthErrorCode errorCode;

    protected GatewayAuthenticationException(AuthErrorCode errorCode, String internalMessage, Throwable cause) {
        super(internalMessage, cause, false, false);
        this.errorCode = errorCode;
    }

    public AuthErrorCode getErrorCode() {
        return errorCode;
    }
}
