package com.example.eduspace.common.dto;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateResponse {

    private String id;

    private String title;

    private String url;

    private Instant uploadedAt;
}