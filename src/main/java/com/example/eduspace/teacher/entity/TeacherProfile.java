package com.example.eduspace.teacher.entity;

import com.example.eduspace.common.entity.*;
import com.example.eduspace.common.enums.Gender;

import lombok.*;
import lombok.experimental.SuperBuilder;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "teacher_profiles")
public class TeacherProfile extends BaseEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field(targetType = FieldType.OBJECT_ID)
    private String userId;

    // ---- Step 1: Basic Info ----
    private String name;

    private String phoneNumber;

    private Address address;

    private Gender gender;

    // ---- Display / LinkedIn-style header ----
    private String headline;

    private String about;

    private String avatarUrl;

    private String coverImageUrl;

    private String resumeUrl;

    @Builder.Default
    private List<Certificate> certificates = List.of();

    // ---- Step 2: Educational Details ----
    @Builder.Default
    private List<Education> education = List.of();

    // ---- Step 3: Subjects Offered (extendable post-onboarding) ----
    @Builder.Default
    private List<SubjectOffering> subjectOfferings = List.of();

    // ---- Step 4: Location + Face Verification ----
    @Builder.Default
    private ProfileVerification verification = ProfileVerification.builder().build();

    // ---- Step 5: Review & Submit ----
    private boolean profileCompleted;

    @Builder.Default
    private int profileCompletionPercent = 0;

    @Builder.Default
    private long profileViews = 0;
}