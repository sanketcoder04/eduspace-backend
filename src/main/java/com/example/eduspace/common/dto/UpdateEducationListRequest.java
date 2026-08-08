package com.example.eduspace.common.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * Replaces the full education list in a single call. The wizard/profile page
 * submits the whole set of entries (add/edit/remove are all local UI state
 * until this is saved), which keeps the API surface small.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEducationListRequest {

    @NotNull(message = "Education list is required.")
    @Valid
    private List<EducationDto> education;
}