package com.example.eduspace.post.entity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollOption {

    private String id;

    private String text;

    @Builder.Default
    private int votesCount = 0;
}