package com.example.eduspace.chat.dto.response;

import com.example.eduspace.chat.enums.ConversationStatus;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {

    private String id;

    private String applicationId;

    private String opportunityId;

    private String opportunityTitle;

    private String authorId;

    private String authorName;

    private String authorAvatarUrl;

    private String applicantId;

    private String applicantName;

    private String applicantAvatarUrl;

    private ConversationStatus status;

    private String lastMessagePreview;

    private Instant lastMessageAt;

    private Instant createdAt;
}