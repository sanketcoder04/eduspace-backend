package com.example.eduspace.application.dto.response;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactShareConsentResponse {

    private boolean phoneShared;

    private boolean emailShared;

    private String phoneNumber; // populated only when phoneShared is true

    private String email;       // populated only when emailShared is true

    private Instant updatedAt;
}