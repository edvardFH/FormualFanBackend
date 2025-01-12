package com.onesquad.formulafan.adapter.controller;

import com.onesquad.formulafan.application.exception.FieldValueAlreadyUsedException;
import com.onesquad.formulafan.application.exception.IllegalOperationException;
import com.onesquad.formulafan.application.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleExceptions(Exception ex) {
        HttpStatus status = switch (ex) {
            case FieldValueAlreadyUsedException e -> HttpStatus.FORBIDDEN;
            case IllegalOperationException e -> HttpStatus.UNAUTHORIZED;
            case AuthenticationException e -> HttpStatus.UNAUTHORIZED;
            case ResourceNotFoundException e -> HttpStatus.NOT_FOUND;
            case IllegalArgumentException e -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            response.put("details", "An unexpected error occurred");
        }

        return ResponseEntity.status(status).body(response);
    }
}
