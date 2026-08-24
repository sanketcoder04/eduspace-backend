package com.example.eduspace.opportunity.dto.response;

import com.example.eduspace.opportunity.enums.FeeUnit;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeRangeResponse {

    private Double min;

    private Double max;

    private String currency;

    private FeeUnit unit;
}