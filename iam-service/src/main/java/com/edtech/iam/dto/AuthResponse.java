package com.edtech.iam.dto;

// The JWT itself is never placed here — it only ever travels as an HttpOnly cookie.
public record AuthResponse(
        String message,
        UserResponse user
) {}
