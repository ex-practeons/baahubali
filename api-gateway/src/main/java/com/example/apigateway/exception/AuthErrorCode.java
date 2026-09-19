package com.example.apigateway.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
public enum AuthErrorCode {

    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "Authentication is required to access this resource."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "The authentication token is invalid."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "The authentication token has expired. Please sign in again.");

    private final HttpStatus status;
    private final String clientMessage;
}