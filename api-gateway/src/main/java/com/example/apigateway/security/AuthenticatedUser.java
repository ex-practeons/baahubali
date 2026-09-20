package com.example.apigateway.security;

public record AuthenticatedUser(String userId, String role) {
}