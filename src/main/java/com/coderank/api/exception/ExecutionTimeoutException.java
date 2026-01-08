package com.coderank.api.exception;

public class ExecutionTimeoutException extends RuntimeException {
    public ExecutionTimeoutException(String message) {
        super(message);
    }
}

