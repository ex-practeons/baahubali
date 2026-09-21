package com.edtech.iam.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        // Deliberately generic — never reveal whether the email or the password was wrong.
        super("Invalid email or password");
    }
}
