package com.example.eduspace.teacher.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectOfferingResponse {

    private String id;

    private String subjectName;

    private String qualificationLevel;

    private String resumeUrl;

    private List<String> certificateUrls;

    private Instant addedAt;

    private Instant updatedAt;
}