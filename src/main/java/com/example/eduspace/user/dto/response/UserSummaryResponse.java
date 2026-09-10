package com.example.eduspace.user.dto.response;

import com.example.eduspace.common.enums.Role;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {

    private String id;

    private String name;

    private Role role;

    private Instant lastLoginAt;
}