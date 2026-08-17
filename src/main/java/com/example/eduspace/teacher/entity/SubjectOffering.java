package com.example.eduspace.teacher.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

/**
 * A single "I teach X" entry. Teachers add these incrementally after
 * onboarding, so it's modelled as its own sub-resource (own id, own
 * add/update/delete endpoints) rather than being locked into the wizard.
 */
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