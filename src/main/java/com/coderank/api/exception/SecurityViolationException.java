package com.coderank.api.exception;

public class SecurityViolationException extends RuntimeException {
    public SecurityViolationException(String message) {
        super(message);
    }
}

