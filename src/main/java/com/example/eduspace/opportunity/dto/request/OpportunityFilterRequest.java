package com.example.eduspace.opportunity.dto.request;

import com.example.eduspace.opportunity.enums.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityFilterRequest {

    private PostType postType;             // usually fixed per feed tab (Teaching Openings vs Tuition Requirements)

    private List<String> cities;           // multi-select

    private List<Mode> modes;              // multi-select

    private List<ClassFormat> classFormats; // multi-select

    private List<String> subjects;         // multi-select

    private Double minFee;

    private Double maxFee;

    private Instant postedAfter;           // "date posted" filter, e.g. now-7d / now-30d

    private List<OpportunityStatus> statuses; // used by "my posts" view; feed defaults to OPEN/PARTIALLY_FILLED

    private String authorId;               // used by "my posts" view
}