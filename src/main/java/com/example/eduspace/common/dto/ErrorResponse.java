package com.example.eduspace.common.dto;

import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private boolean success;

    private String message;

    private int status;

    private Map<String, String> errors;

    @Builder.Default
    private Instant timestamp = Instant.now();

}