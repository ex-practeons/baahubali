package com.example.testservice.service;

import com.example.testservice.exception.ValidationException;

public class MathValidationUtil {

    /**
     * Checks if a string contains balanced unescaped dollar signs '$'.
     * A basic check to ensure LaTeX math mode delimiters are closed.
     * Escaped dollars ('\$') do not count towards the balance.
     */
    public static void validateBalancedMathDelimiters(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }

        long dollarCount = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '$') {
                // If it's escaped by a backslash, ignore it
                if (i > 0 && text.charAt(i - 1) == '\\') {
                    // Check if the backslash itself was escaped (e.g., \\$)
                    int backslashCount = 0;
                    for (int j = i - 1; j >= 0; j--) {
                        if (text.charAt(j) == '\\') {
                            backslashCount++;
                        } else {
                            break;
                        }
                    }
                    if (backslashCount % 2 != 0) {
                        continue; // The dollar sign is escaped
                    }
                }
                dollarCount++;
            }
        }

        if (dollarCount % 2 != 0) {
            throw new ValidationException("Unbalanced '$' delimiter in question or option text");
        }
    }
}
