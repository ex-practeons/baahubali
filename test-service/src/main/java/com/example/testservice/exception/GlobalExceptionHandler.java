package com.example.testservice.exception;

import com.example.testservice.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(ValidationException ex) {
        ApiErrorResponse.ErrorPayload payload = new ApiErrorResponse.ErrorPayload(
                "VALIDATION_ERROR",
                ex.getMessage(),
                ex.getErrors() != null ? ex.getErrors() : Collections.emptyList()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse(payload));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(ResourceNotFoundException ex) {
        ApiErrorResponse.ErrorPayload payload = new ApiErrorResponse.ErrorPayload(
                "NOT_FOUND",
                ex.getMessage(),
                Collections.emptyList()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(payload));
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflictException(ResourceConflictException ex) {
        ApiErrorResponse.ErrorPayload payload = new ApiErrorResponse.ErrorPayload(
                "CONFLICT",
                ex.getMessage(),
                Collections.emptyList()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(payload));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        ApiErrorResponse.ErrorPayload payload = new ApiErrorResponse.ErrorPayload(
                "FORBIDDEN",
                ex.getReason() != null ? ex.getReason() : "Access Denied",
                Collections.emptyList()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(new ApiErrorResponse(payload));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        log.error("Unhandled exception occurred: ", ex);
        ApiErrorResponse.ErrorPayload payload = new ApiErrorResponse.ErrorPayload(
                "INTERNAL_ERROR",
                "An unexpected server error occurred.",
                Collections.emptyList()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse(payload));
    }
}