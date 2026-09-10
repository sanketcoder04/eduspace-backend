package com.example.eduspace.post.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollOptionResponse {

    private String id;

    private String text;

    private int votesCount;

    private double votePercentage;
}