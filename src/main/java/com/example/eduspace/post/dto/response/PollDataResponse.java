package com.example.eduspace.post.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollDataResponse {

    private List<PollOptionResponse> options;

    private boolean allowMultipleChoice;

    private Instant expiresAt;

    private int totalVotes;

    private boolean expired;
}