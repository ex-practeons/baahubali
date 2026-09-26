package com.example.testservice.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "internal.auth")
public class InternalAuthInterceptor implements HandlerInterceptor {

    private final Map<String, String> allowedClients = new HashMap<>();

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
            reject(response, "Missing internal caller identification or auth token.");
            return false;
        }

        String expectedSecret = allowedClients.get(caller);

        if (expectedSecret == null || !expectedSecret.equals(authHeader)) {
            reject(response, "Invalid credentials for caller: " + caller);
            return false;
        }

        return true;
    }

    private void reject(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("Unauthorized: " + message);
    }
}