package com.example.eduspace.teacher.entity;

import com.example.eduspace.common.entity.BaseEntity;
import com.example.eduspace.common.enums.Gender;

import lombok.*;
import lombok.experimental.SuperBuilder;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private String userId;

    private String name;

    private String phoneNumber;

    private Gender gender;

    private String profileImage;

    private boolean profileCompleted;
}