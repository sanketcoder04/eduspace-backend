package com.example.eduspace.refresh.entity;

import com.example.eduspace.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refresh_tokens")
public class RefreshToken extends BaseEntity {

    @Id
    private String id;

    private String userId;

    private String token;

    private Instant expiresAt;

    private boolean revoked;

    private Instant revokedAt;
}