package com.example.eduspace.post.entity;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollData {

    private List<PollOption> options;

    @Builder.Default
    private boolean allowMultipleChoice = false;

    private Instant expiresAt;

    @Builder.Default
    private int totalVotes = 0;
}