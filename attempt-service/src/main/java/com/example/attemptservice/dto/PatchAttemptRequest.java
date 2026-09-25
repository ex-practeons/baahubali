package com.example.attemptservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatchAttemptRequest {
    private String questionId;
    private String selectedOption;
    private Integer currentQuestionIndex;
    private Long version;
}