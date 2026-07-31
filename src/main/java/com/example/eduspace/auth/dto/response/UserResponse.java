package com.example.eduspace.auth.dto.response;

import com.example.eduspace.common.enums.Role;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private String id;

    private String name;

    private String email;

    private Role role;

    private boolean emailVerified;

    private Instant lastLoginAt;

    private Instant createdAt;

    private Instant updatedAt;
}