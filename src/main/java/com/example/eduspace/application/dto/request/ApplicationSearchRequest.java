package com.example.eduspace.application.dto.request;

import com.example.eduspace.application.enums.ApplicationBox;
import com.example.eduspace.application.enums.ApplicationSort;
import com.example.eduspace.application.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationSearchRequest {

    @NotNull(message = "Box is required.")
    private ApplicationBox box;

    private List<ApplicationStatus> statuses;

    private String keyword;

    private Instant postedAfter;

    private ApplicationSort sort;
}