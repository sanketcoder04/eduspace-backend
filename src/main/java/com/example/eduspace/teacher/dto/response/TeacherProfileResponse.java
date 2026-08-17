package com.example.eduspace.teacher.dto.response;

import com.example.eduspace.common.dto.AddressDto;
import com.example.eduspace.common.dto.CertificateResponse;
import com.example.eduspace.common.dto.EducationDto;
import com.example.eduspace.common.dto.VerificationResponse;
import com.example.eduspace.common.enums.Gender;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherProfileResponse {

    private String id;

    private String userId;

    private String email;

    private String name;

    private String phoneNumber;

    private AddressDto address;

    private Gender gender;

    private String headline;

    private String about;

    private String avatarUrl;

    private String coverImageUrl;

    private String resumeUrl;

    private List<CertificateResponse> certificates;

    private List<EducationDto> education;

    private List<SubjectOfferingResponse> subjectOfferings;

    private VerificationResponse verification;

    private boolean profileCompleted;

    private int profileCompletionPercent;

    private long profileViews;

    private Instant createdAt;

    private Instant updatedAt;

    private Instant lastLoginAt;
}