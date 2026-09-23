package com.example.testservice.dto.admin;

import java.util.List;

public record BulkImportResultDto(
        int imported,
        int failed,
        List<ImportError> errors
) {
    public record ImportError(int row, String reason) {}
}