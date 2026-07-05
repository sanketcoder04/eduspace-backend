package com.example.eduspace.common.util;

import com.example.eduspace.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class ResponseUtil {

    private ResponseUtil() {}

    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .success(true)
                        .message(message)
                        .data(data)
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<T>builder()
                                .success(true)
                                .message(message)
                                .data(data)
                                .build()
                );
    }
}
