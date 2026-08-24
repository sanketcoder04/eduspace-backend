package com.example.eduspace.opportunity.dto.response;

import lombok.*;
import java.time.DayOfWeek;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotResponse {

    private DayOfWeek day;

    private String startTime;

    private String endTime;
}