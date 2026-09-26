package com.example.attemptservice.exception;

public class AttemptNotFoundException extends RuntimeException {

    public AttemptNotFoundException(String attemptId) {
        super("Attempt not found: " + attemptId);
    }
}