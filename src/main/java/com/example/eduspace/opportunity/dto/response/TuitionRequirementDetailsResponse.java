package com.example.eduspace.opportunity.dto.response;

import com.example.eduspace.opportunity.enums.PreferredTutorExperienceLevel;
import com.example.eduspace.opportunity.enums.PreferredTutorGender;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TuitionRequirementDetailsResponse {

    private PreferredTutorGender preferredTutorGender;

    private PreferredTutorExperienceLevel preferredTutorExperienceLevel;

    private int numberOfStudents;

    private String additionalRequirements;
}