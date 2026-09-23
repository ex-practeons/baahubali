package com.example.testservice.dto.catalog;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class TestSeriesResponseDto {
    private String id;
    private String categoryId;
    private String title; // resolved based on requested lang
    private BigDecimal basePrice;
    private Boolean isFree;
    private List<String> requiredLanguages;
}
