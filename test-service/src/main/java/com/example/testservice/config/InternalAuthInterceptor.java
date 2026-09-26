package com.example.testservice.config;

import com.example.testservice.dto.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Collections;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "internal.auth")
public class InternalAuthInterceptor implements HandlerInterceptor {

    private final Map<String, String> allowedClients = new HashMap<>();
    private final ObjectMapper objectMapper;

    public InternalAuthInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, String> getAllowedClients() {
        return allowedClients;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String caller = request.getHeader("X-Service-Caller");
        String authHeader = request.getHeader("X-Service-Auth");

        if (caller == null || authHeader == null) {
            reject(response);
            return false;
        }

        String expectedSecret = allowedClients.get(caller);

        if (expectedSecret == null || !expectedSecret.equals(authHeader)) {
            reject(response);
            return false;
        }

        return true;
    }

    private void reject(HttpServletResponse response) throws Exception {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), new ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Internal service authentication failed",
                "INTERNAL_AUTH_FAILED",
                Collections.emptyList()));
    }
}
