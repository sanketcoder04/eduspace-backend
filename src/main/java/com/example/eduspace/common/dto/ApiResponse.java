package com.example.eduspace.common.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;

    private String message;

    private T data;

    @Builder.Default
    private Instant timestamp = Instant.now();
}
