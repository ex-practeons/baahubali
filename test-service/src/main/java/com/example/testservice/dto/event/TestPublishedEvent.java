package com.example.testservice.dto.event;

import java.util.UUID;

public record TestPublishedEvent(
        UUID testId,
        UUID seriesId,
        String title,
        java.time.Instant publishedAt
) {}