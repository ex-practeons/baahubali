package com.example.testservice.validation;

import com.example.testservice.entity.QuestionType;
import java.util.List;
import java.util.Map;

public class QuestionValidationUtil {

    public static boolean isValidAnswerSchema(QuestionType type, Map<String, Object> correctAnswer, Map<String, Object> options) {
        if (correctAnswer == null || correctAnswer.isEmpty()) {
            return false;
        }

        return switch (type) {
            case MCQ -> {
                // Must have exactly one correct option key, and that key must exist in options
                String correctKey = (String) correctAnswer.get("key");
                yield correctKey != null && options != null && options.containsKey(correctKey);
            }
            case MULTI_CORRECT -> {
                // Must be a list of keys, all of which exist in options
                Object keysObj = correctAnswer.get("keys");
                if (!(keysObj instanceof List<?> keys)) yield false;
                if (options == null || keys.isEmpty()) yield false;
                
                yield keys.stream().allMatch(k -> options.containsKey((String) k));
            }
            case NUMERICAL -> {
                // Must have a numeric value and an optional tolerance. Options JSON is irrelevant here.
                yield correctAnswer.containsKey("value") && correctAnswer.get("value") instanceof Number;
            }
            case SUBJECTIVE -> {
                // Usually evaluated manually, but might have a rubric in the answer key
                yield true;
            }
            default -> false;
        };
    }
}