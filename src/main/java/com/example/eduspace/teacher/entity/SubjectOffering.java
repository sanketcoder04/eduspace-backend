package com.example.eduspace.teacher.entity;

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
public class SubjectOffering {

    private String id;

    private String subjectName;

    private String qualificationLevel;

    private Instant addedAt;

    private Instant updatedAt;
}