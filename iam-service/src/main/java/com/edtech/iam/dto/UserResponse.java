package com.edtech.iam.dto;

import com.edtech.iam.entity.Role;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        Role role,
        Instant createdAt
) {}
