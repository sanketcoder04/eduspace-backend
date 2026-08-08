package com.example.eduspace.teacher.dto.response;

import com.example.eduspace.common.dto.AddressDto;
import com.example.eduspace.common.dto.EducationDto;
import com.example.eduspace.common.enums.Gender;
import lombok.*;

import java.util.List;

/**
 * Full profile payload — this is what powers the LinkedIn-style profile page,
 * so it intentionally carries every section in one response rather than
 * requiring the frontend to stitch together multiple calls.
 */
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

    private List<EducationDto> education;

    private List<SubjectOfferingResponse> subjectOfferings;

    private VerificationResponse verification;

    private boolean profileCompleted;

    private int profileCompletionPercent;
}