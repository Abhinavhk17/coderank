package com.coderank.api.exception;

import com.coderank.api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExecutionTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleExecutionTimeout(
            ExecutionTimeoutException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.REQUEST_TIMEOUT.value())
                .message(ex.getMessage())
                .error("Execution Timeout")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.REQUEST_TIMEOUT);
    }

    @ExceptionHandler(SecurityViolationException.class)
    public ResponseEntity<ErrorResponse> handleSecurityViolation(
            SecurityViolationException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage())
                .error("Security Violation")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleResourceLimitExceeded(
            ResourceLimitExceededException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INSUFFICIENT_STORAGE.value())
                .message(ex.getMessage())
                .error("Resource Limit Exceeded")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INSUFFICIENT_STORAGE);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(
            RateLimitExceededException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .message(ex.getMessage())
                .error("Rate Limit Exceeded")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("Invalid username or password")
                .error("Authentication Failed")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(
            UsernameNotFoundException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .error("User Not Found")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("message", errors);
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .error("Internal Server Error")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

