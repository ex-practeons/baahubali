package com.example.apigateway.exception;

import org.springframework.http.HttpStatus;

public interface GatewayErrorCode {
    HttpStatus getStatus();
    String getClientMessage();
    String name();
}