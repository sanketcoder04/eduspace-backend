package com.example.eduspace.follow.dto.response;

import com.example.eduspace.common.enums.Role;
import lombok.*;

import java.time.Instant;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FollowedUserResponse {

    private String userId;

    private String name;

    private String avatarUrl;

    private Role role;

    private Instant followedAt;
}