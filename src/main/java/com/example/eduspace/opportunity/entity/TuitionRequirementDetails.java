package com.example.eduspace.opportunity.entity;

import com.example.eduspace.opportunity.enums.PreferredTutorExperienceLevel;
import com.example.eduspace.opportunity.enums.PreferredTutorGender;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TuitionRequirementDetails {

    private PreferredTutorGender preferredTutorGender;

    private PreferredTutorExperienceLevel preferredTutorExperienceLevel;

    @Builder.Default
    private int numberOfStudents = 1;

    private String additionalRequirements;
}