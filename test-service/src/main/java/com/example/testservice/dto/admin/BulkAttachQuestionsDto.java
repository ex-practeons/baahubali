package com.example.testservice.dto.admin;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record BulkAttachQuestionsDto(
        List<UUID> questionIds,
        BigDecimal positiveMarksOverride,
        BigDecimal negativeMarksOverride
) {}