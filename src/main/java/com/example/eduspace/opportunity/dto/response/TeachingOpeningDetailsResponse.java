package com.example.eduspace.opportunity.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeachingOpeningDetailsResponse {

    private Integer batchCapacity;

    private int seatsFilled;

    private List<TimeSlotResponse> availableSlots;

    private String languageOfInstruction;

    private boolean freeDemoAvailable;

    private Integer yearsOfExperienceInSubject;
}