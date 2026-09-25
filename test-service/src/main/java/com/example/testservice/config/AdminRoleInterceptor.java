package com.example.testservice.config;

import com.example.testservice.dto.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class AdminRoleInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String role = request.getHeader("X-User-Role");

        System.out.println ("ROLE -------------->" + role);
        
        if (role == null || (!role.equals("ADMIN"))) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), new ApiErrorResponse(
                    HttpStatus.FORBIDDEN.value(), "Access Denied: Insufficient Role",
                    "FORBIDDEN", Collections.emptyList()));
            return false; 
        }
        
        return true;
    }
}
