package com.example.attemptservice.exception;

import com.example.attemptservice.dto.ApiResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AttemptNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(AttemptNotFoundException ex) {
        return failure(HttpStatus.NOT_FOUND, "ATTEMPT_NOT_FOUND", ex.getMessage(), null);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        return failure(ex.getStatusCode(), status == null ? "REQUEST_FAILED" : status.name(),
                ex.getReason() == null ? "Request failed" : ex.getReason(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of("field", error.getField(), "issue",
                        error.getDefaultMessage() == null ? "Invalid value" : error.getDefaultMessage()))
                .toList();
        return failure(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "Validation failed for the submitted input", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return failure(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "Request body is missing or malformed", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        return failure(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred", null);
    }

    private ResponseEntity<ApiResponse<Void>> failure(HttpStatusCode status, String code, String message, Object details) {
        ApiResponse<Void> response = ApiResponse.failure(status.value(), message, new ApiResponse.ApiError(code, details));
        return ResponseEntity.status(status).body(response);
    }
}
