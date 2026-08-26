package com.example.eduspace.opportunity.entity;

import lombok.*;
import java.time.DayOfWeek;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlot {

    private DayOfWeek day;

    private String startTime; // "17:00" (24h, stored as string to keep it timezone-agnostic at this layer)

    private String endTime;
}