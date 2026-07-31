package com.example.eduspace.user.entity;

import com.example.eduspace.common.entity.BaseEntity;
import com.example.eduspace.common.enums.AuthProvider;
import com.example.eduspace.common.enums.Role;

import lombok.*;
import lombok.experimental.SuperBuilder;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String name;

    private Role role;

    private AuthProvider provider;

    private String providerId;

    private boolean emailVerified;

    private Instant emailVerifiedAt;

    private boolean enabled;

    private boolean accountLocked;

    private Instant lastLoginAt;
}