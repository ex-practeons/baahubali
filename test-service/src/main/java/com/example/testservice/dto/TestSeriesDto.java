package com.example.testservice.dto;

import com.example.testservice.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestSeriesDto {
    private String id;
    private String categoryId;
    private java.util.Map<String, String> titleTranslations;
    private BigDecimal basePrice;
    private Boolean isFree;
    private Status status;
}
