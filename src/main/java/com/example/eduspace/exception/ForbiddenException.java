package com.example.eduspace.exception;

public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super(message);
    }
}
