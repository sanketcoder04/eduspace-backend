package com.example.eduspace.chat.dto.response;

import com.example.eduspace.chat.enums.MessageType;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private String id;

    private String conversationId;

    private String senderId;      // null for SYSTEM / CONTACT_SHARE_UPDATE messages

    private String senderName;    // enriched; also null for those message types

    private MessageType type;

    private String content;

    private boolean read;

    private Instant createdAt;
}