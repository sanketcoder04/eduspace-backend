package com.example.eduspace.post.dto.response;

import com.example.eduspace.common.enums.Role;
import com.example.eduspace.post.enums.PostType;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private String id;

    private String authorId;

    private Role authorRole;

    private String authorName;

    private String authorAvatarUrl;

    private PostType type;

    private String content;

    private List<String> mediaUrls;

    private String documentUrl;

    private String documentFileName;

    private PollDataResponse poll;

    private List<String> mentions;

    private int likesCount;

    private int commentsCount;

    private boolean likedByViewer;

    private List<String> viewerSelectedOptionIds;

    private boolean edited;

    private Instant createdAt;

    private Instant updatedAt;
}