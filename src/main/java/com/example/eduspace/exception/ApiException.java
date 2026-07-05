package com.example.eduspace.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}