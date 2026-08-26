package com.example.eduspace.opportunity.entity;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeachingOpeningDetails {

    private Integer batchCapacity;       // only when classFormat = BATCH

    @Builder.Default
    private int seatsFilled = 0;

    private List<TimeSlot> availableSlots;

    private String languageOfInstruction;

    private boolean freeDemoAvailable;

    private Integer yearsOfExperienceInSubject;
}