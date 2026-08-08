package com.example.eduspace.student.dto.response;

import com.example.eduspace.common.dto.AddressDto;
import com.example.eduspace.common.dto.EducationDto;
import com.example.eduspace.common.dto.VerificationResponse;
import com.example.eduspace.common.enums.Gender;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileResponse {

    private String id;

    private String userId;

    private String email;

    private String name;

    private String phoneNumber;

    private AddressDto address;

    private String parentName;

    private String parentPhoneNumber;

    private String parentEmail;

    private Gender gender;

    private String headline;

    private String about;

    private String avatarUrl;

    private List<EducationDto> education;

    private VerificationResponse verification;

    private boolean profileCompleted;

    private int profileCompletionPercent;
}