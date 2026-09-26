package com.example.apigateway.exception;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webflux.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Formats unhandled gateway errors, including unmatched routes, with the shared API error envelope. */
@Component
public class GatewayErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request);
        HttpStatusCode status = error instanceof ErrorResponse errorResponse
                ? errorResponse.getStatusCode()
                : HttpStatus.INTERNAL_SERVER_ERROR;
        HttpStatus knownStatus = HttpStatus.resolve(status.value());
        String code = status.value() == HttpStatus.NOT_FOUND.value()
                ? "NOT_FOUND"
                : knownStatus == null ? "HTTP_ERROR" : knownStatus.name();
        String message = status.value() == HttpStatus.NOT_FOUND.value()
                ? "The requested resource was not found."
                : status.is5xxServerError()
                    ? "An unexpected gateway error occurred."
                    : knownStatus == null ? "The request could not be completed." : knownStatus.getReasonPhrase();

        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("code", code);
        errorBody.put("details", List.of());

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("timestamp", Instant.now().toString());
        meta.put("trace_id", "err-" + UUID.randomUUID());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("status", status.value());
        body.put("message", message);
        body.put("error", errorBody);
        body.put("meta", meta);
        return body;
    }
}
