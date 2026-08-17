package com.example.eduspace.teacher.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddSubjectOfferingRequest {

    @NotBlank(message = "Subject name is required.")
    private String subjectName;

    @NotBlank(message = "Qualification level is required.")
    private String qualificationLevel;
}