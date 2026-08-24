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

    private Instant updatedAt;
}