package com.example.eduspace.follow.dto.response;

import com.example.eduspace.common.enums.Role;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedProfileResponse {

    private String userId;

    private String name;

    private String avatarUrl;

    private Role role;
}