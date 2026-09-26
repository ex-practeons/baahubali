package com.example.attemptservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GatewaySecurityFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Only secure external-facing endpoints routed by API Gateway
        if (request.getRequestURI().startsWith("/api/")) {
            String userId = request.getHeader("X-User-Id");
            
            // If the header is missing, the request did not come through the trusted Gateway
            if (userId == null || userId.trim().isEmpty()) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Unauthorized: Missing X-User-Id Header. Direct external access prohibited.");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
}