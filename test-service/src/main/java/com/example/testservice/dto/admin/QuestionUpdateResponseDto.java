package com.example.testservice.dto.admin;

import java.util.UUID;

public record QuestionUpdateResponseDto(
        UUID questionId,
        String warning // Null if question is not locked
) {}