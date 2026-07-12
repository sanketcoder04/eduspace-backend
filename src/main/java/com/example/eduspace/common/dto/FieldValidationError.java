package com.example.eduspace.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FieldValidationError {

    private String field;

    private String message;
}