package com.example.eduspace.application.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectApplicationRequest {

    private String reason; // optional
}