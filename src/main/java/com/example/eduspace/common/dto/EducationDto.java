package com.example.eduspace.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationDto {

    // Present when editing an existing entry; null/blank when adding a new one.
    private String id;

    @NotBlank(message = "Institution name is required.")
    private String institution;

    @NotBlank(message = "Degree/qualification is required.")
    private String degree;

    private String fieldOfStudy;

    private String board;

    @NotNull(message = "Start year is required.")
    private Integer startYear;

    private Integer endYear;
}