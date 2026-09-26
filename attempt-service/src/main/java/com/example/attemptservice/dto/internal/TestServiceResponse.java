package com.example.attemptservice.dto.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TestServiceResponse<T>(boolean success, int status, String message, T data) {
}
