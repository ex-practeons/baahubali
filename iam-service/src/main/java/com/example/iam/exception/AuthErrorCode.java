package com.example.apigateway.exception;

import org.springframework.http.HttpStatus;

public enum AuthErrorCode {
    MISSING_TOKEN(HttpStatus.UNAUTHORIZED, "Authentication token is missing or not provided."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Authentication token is invalid, tampered, or missing required claims."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Authentication token has expired. Please log in again."),
    SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "Session expired or user logged in from another device."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "You do not have the required permissions to access this resource.");

    private final HttpStatus status;
    private final String clientMessage;

    AuthErrorCode(HttpStatus status, String clientMessage) {
        this.status = status;
        this.clientMessage = clientMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getClientMessage() {
        return clientMessage;
    }
}