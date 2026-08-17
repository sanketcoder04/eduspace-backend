package com.example.eduspace.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCertificateRequest {

    @NotBlank(message = "Certificate title is required.")
    private String title;

    @NotBlank(message = "Certificate file URL is required.")
    private String url;
}