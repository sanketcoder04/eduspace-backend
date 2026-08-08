package com.example.eduspace.student.entity;

import com.example.eduspace.common.entity.Address;
import com.example.eduspace.common.entity.BaseEntity;
import com.example.eduspace.common.entity.Education;
import com.example.eduspace.common.entity.ProfileVerification;
import com.example.eduspace.common.enums.Gender;

import lombok.*;
import lombok.experimental.SuperBuilder;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "student_profiles")
public class StudentProfile extends BaseEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    // ---- Step 1: Basic Info ----
    private String name;

    private String phoneNumber;

    private Address address;

    private String parentName;

    private String parentPhoneNumber;

    private String parentEmail;

    private Gender gender;

    // ---- Display / LinkedIn-style header ----
    private String headline;

    private String about;

    private String avatarUrl;

    // ---- Step 2: Educational Details ----
    @Builder.Default
    private List<Education> education = List.of();

    // ---- Step 3: Location + Face Verification ----
    @Builder.Default
    private ProfileVerification verification = ProfileVerification.builder().build();

    // ---- Step 4: Review & Submit ----
    private boolean profileCompleted;

    @Builder.Default
    private int profileCompletionPercent = 0;
}