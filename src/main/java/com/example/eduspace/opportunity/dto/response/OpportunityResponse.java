package com.example.eduspace.opportunity.dto.response;

import com.example.eduspace.common.dto.AddressDto;
import com.example.eduspace.common.enums.Role;
import com.example.eduspace.opportunity.enums.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityResponse {

    private String id;

    private String authorId;

    private Role authorRole;

    // Enriched by the service after mapping — not derivable from the Opportunity document alone.
    private String authorName;

    private String authorAvatarUrl;

    private PostType postType;

    private String title;

    private List<String> subjects;

    private String gradeLevel;

    private String board;

    private String description;

    private Mode mode;

    private ClassFormat classFormat;

    private AddressDto location;

    private TuitionLocationType tuitionLocationType;

    private FeeRangeResponse feeRange;

    private Double sessionDurationHours;

    private Integer sessionsPerWeek;

    private LocalDate preferredStartDate;

    private OpportunityStatus status;

    private int applicationsCount;

    private TeachingOpeningDetailsResponse teachingOpeningDetails;

    private TuitionRequirementDetailsResponse tuitionRequirementDetails;

    private Instant createdAt;

    private Instant updatedAt;
}