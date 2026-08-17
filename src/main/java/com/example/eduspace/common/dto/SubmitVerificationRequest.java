package com.example.eduspace.common.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitVerificationRequest {

    @NotBlank(message = "Selfie is required for face verification.")
    private String selfieUrl;

    @NotNull(message = "Address is required for location verification.")
    @Valid
    private AddressDto address;
}