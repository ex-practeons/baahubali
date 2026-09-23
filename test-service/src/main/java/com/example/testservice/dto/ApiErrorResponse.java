package com.example.testservice.dto;

import java.util.List;

public record ApiErrorResponse(ErrorPayload error) {
    public record ErrorPayload(String code, String message, List<String> details) {}
}