package com.example.iam.dto;

import com.example.iam.entity.Role;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        Role role,
        Instant createdAt
) {}
