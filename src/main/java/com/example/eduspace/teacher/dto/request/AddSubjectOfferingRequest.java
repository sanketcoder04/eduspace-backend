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

    // URLs returned by POST /api/v1/media/upload, uploaded beforehand.
    private String resumeUrl;

    @Builder.Default
    private List<String> certificateUrls = List.of();
}