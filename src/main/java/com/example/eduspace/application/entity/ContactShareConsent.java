package com.example.eduspace.application.entity;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactShareConsent {
    @Builder.Default
    private boolean phoneShared = false;
    @Builder.Default
    private boolean emailShared = false;
    private Instant updatedAt;
}