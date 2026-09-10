package com.example.eduspace.post.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private String id;

    private String postId;

    private String authorId;

    private String authorName;

    private String authorAvatarUrl;

    private String content;

    private List<String> mentions;

    private String parentCommentId;

    private long repliesCount;

    private int likesCount;

    private boolean likedByViewer;

    private boolean edited;

    private Instant createdAt;
}