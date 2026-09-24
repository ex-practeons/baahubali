package com.example.iam.exception;

import com.example.iam.dto.ApiErrorDetail;
import com.example.iam.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "An account with this email already exists",
                "EMAIL_ALREADY_EXISTS",
                List.of()
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                "INVALID_CREDENTIALS",
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<ApiErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ApiErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed for the submitted input",
                "INVALID_INPUT",
                details
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableRequest(HttpMessageNotReadableException ex) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Request body is missing or malformed",
                "INVALID_INPUT",
                List.of()
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return buildResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Content-Type must be application/json",
                "UNSUPPORTED_MEDIA_TYPE",
                List.of()
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingRequestHeader(MissingRequestHeaderException ex) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "A required request header is missing",
                "MISSING_REQUIRED_HEADER",
                List.of(new ApiErrorDetail(ex.getHeaderName(), "Header is required"))
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception for {} {}", request.getMethod(), request.getRequestURI(), ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                "INTERNAL_SERVER_ERROR",
                List.of()
        );
    }

    private ResponseEntity<ApiResponse<Void>> buildResponse(
            HttpStatus status, String message, String code, List<ApiErrorDetail> details) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(status.value(), message, code, details));
    }
}
