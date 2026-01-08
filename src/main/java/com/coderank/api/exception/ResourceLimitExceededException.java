package com.coderank.api.exception;

public class ResourceLimitExceededException extends RuntimeException {
    public ResourceLimitExceededException(String message) {
        super(message);
    }
}

