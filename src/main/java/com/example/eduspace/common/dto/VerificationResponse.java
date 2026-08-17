package com.example.eduspace.common.dto;

import com.example.eduspace.common.enums.VerificationStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationResponse {

    private VerificationStatus status;

    private String selfieUrl;

    private AddressDto verifiedAddress;

    private boolean locationVerified;

    private boolean faceVerified;

    private String rejectionReason;

    private Instant submittedAt;

    private Instant verifiedAt;
}