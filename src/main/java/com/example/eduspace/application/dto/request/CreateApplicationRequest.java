package com.example.eduspace.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateApplicationRequest {

    @NotBlank
    private String opportunityId;

    private String message; // optional note
}