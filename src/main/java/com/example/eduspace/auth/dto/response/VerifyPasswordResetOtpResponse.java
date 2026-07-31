package com.example.eduspace.auth.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyPasswordResetOtpResponse {
    private String resetToken;
}