package com.example.eduspace.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyPasswordResetOtpRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank(message = "OTP is required.")
    private String otp;
}