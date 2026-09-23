package com.example.testservice.service;

import com.example.testservice.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathValidationUtilTest {

    @Test
    void testBalancedDelimiters() {
        assertDoesNotThrow(() -> MathValidationUtil.validateBalancedMathDelimiters("Hello $x$ world"));
        assertDoesNotThrow(() -> MathValidationUtil.validateBalancedMathDelimiters("$$x^2$$ + $$y^2$$"));
        assertDoesNotThrow(() -> MathValidationUtil.validateBalancedMathDelimiters("No math here"));
        assertDoesNotThrow(() -> MathValidationUtil.validateBalancedMathDelimiters("$3\\frac{1}{2}$"));
        assertDoesNotThrow(() -> MathValidationUtil.validateBalancedMathDelimiters("Cost is \\$50 and $x=1$")); // escaped dollar doesn't count
    }

    @Test
    void testUnbalancedDelimiters() {
        ValidationException e1 = assertThrows(ValidationException.class, 
                () -> MathValidationUtil.validateBalancedMathDelimiters("Hello $x world"));
        assertEquals("Unbalanced '$' delimiter in question or option text", e1.getMessage());

        assertThrows(ValidationException.class, 
                () -> MathValidationUtil.validateBalancedMathDelimiters("$$x^2$"));
        
        assertThrows(ValidationException.class, 
                () -> MathValidationUtil.validateBalancedMathDelimiters("$x = 5 and cost is $20")); // unbalanced because one is math and one is not escaped
    }
}
