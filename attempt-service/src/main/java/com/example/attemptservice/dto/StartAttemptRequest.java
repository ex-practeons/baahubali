package com.example.attemptservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StartAttemptRequest {
    private String userId;
    private String testId;
    private Integer durationMinutes;
}