package com.example.eduspace.verification.entity;

import com.example.eduspace.common.entity.BaseEntity;
import com.example.eduspace.user.entity.User;
import com.example.eduspace.verification.enums.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "verification_tokens")
public class VerificationToken extends BaseEntity {

    @Id
    private String id;

    private User user;

    private String token;

    private VerificationType type;

    private Instant expiresAt;

    private boolean used;

    private Instant usedAt;
}