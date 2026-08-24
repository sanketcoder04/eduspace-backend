package com.example.eduspace.opportunity.dto.request;

import com.example.eduspace.common.dto.AddressDto;
import com.example.eduspace.opportunity.entity.FeeRange;
import com.example.eduspace.opportunity.enums.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTuitionRequirementRequest {

    @NotBlank
    private String title;

    @NotEmpty
    private List<String> subjects;

    private String gradeLevel;
    private String board;

    @NotBlank
    private String description;

    @NotNull
    private Mode mode;

    @NotNull
    private ClassFormat classFormat;

    @Valid
    private AddressDto location;

    private TuitionLocationType tuitionLocationType;

    @Valid
    @NotNull
    private FeeRange feeRange; // "fees they can offer"

    @Positive
    private Double sessionDurationHours;

    @Positive
    private Integer sessionsPerWeek;

    private LocalDate preferredStartDate;

    // student-specific
    private PreferredTutorGender preferredTutorGender;

    private PreferredTutorExperienceLevel preferredTutorExperienceLevel;

    @Min(1)
    private Integer numberOfStudents;

    private String additionalRequirements;
}