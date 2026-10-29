package com.example.eduspace.post.dto.response;

import com.example.eduspace.common.enums.Role;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikerResponse {

    private String userId;

    private String name;

    private String avatarUrl;

    private Role role;

    private Instant likedAt;
}