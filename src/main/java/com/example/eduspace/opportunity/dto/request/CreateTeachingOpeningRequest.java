package com.example.eduspace.opportunity.dto.request;

import com.example.eduspace.common.dto.AddressDto;
import com.example.eduspace.opportunity.entity.FeeRange;
import com.example.eduspace.opportunity.entity.TimeSlot;
import com.example.eduspace.opportunity.enums.ClassFormat;
import com.example.eduspace.opportunity.enums.Mode;
import com.example.eduspace.opportunity.enums.TuitionLocationType;
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
public class CreateTeachingOpeningRequest {

    @NotBlank
    private String title;

    @NotEmpty(message = "At least one subject is required.")
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
    private AddressDto location; // required when mode != ONLINE — validated in the service, not here, since it's conditional

    private TuitionLocationType tuitionLocationType;

    @Valid
    @NotNull
    private FeeRange feeRange;

    @Positive
    private Double sessionDurationHours;

    @Positive
    private Integer sessionsPerWeek;

    private LocalDate preferredStartDate;

    // teacher-specific
    private Integer batchCapacity;

    private List<TimeSlot> availableSlots;

    private String languageOfInstruction;

    private boolean freeDemoAvailable;

    private Integer yearsOfExperienceInSubject;
}