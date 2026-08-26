package com.example.eduspace.opportunity.entity;

import com.example.eduspace.opportunity.enums.FeeUnit;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeRange {

    private Double min;

    private Double max;

    @Builder.Default
    private String currency = "INR";

    private FeeUnit unit;
}