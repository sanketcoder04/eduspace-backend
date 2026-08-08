package com.example.eduspace.teacher.entity;

import com.example.eduspace.common.entity.Address;
import com.example.eduspace.common.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherVerification {

    @Builder.Default
    private VerificationStatus status = VerificationStatus.NOT_SUBMITTED;

    private String selfieUrl;

    private Address verifiedAddress;

    private boolean locationVerified;

    private boolean faceVerified;

    private String rejectionReason;

    private Instant submittedAt;

    private Instant verifiedAt;
}