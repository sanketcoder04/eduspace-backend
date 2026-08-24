package com.example.eduspace.application.dto.response;

import com.example.eduspace.application.enums.ApplicationStatus;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {

    private String id;

    private String opportunityId;

    private String opportunityTitle;

    private String applicantId;

    private String applicantName;

    private String applicantAvatarUrl;

    private String authorId;

    private String message;

    private ApplicationStatus status;

    private String decisionReason;

    private Instant respondedAt;

    private ContactShareConsentResponse contactShareConsent;

    private Instant createdAt;
}