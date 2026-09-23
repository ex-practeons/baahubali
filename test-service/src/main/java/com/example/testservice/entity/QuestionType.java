package com.example.testservice.entity;

public enum QuestionType {
    MCQ,            // Single correct option
    MULTI_CORRECT,  // Multiple correct options
    NUMERICAL,      // Numeric answer with optional tolerance
    SUBJECTIVE      // Long-form text (often manually graded)
}