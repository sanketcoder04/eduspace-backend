package com.example.eduspace.chat.dto.ws;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadReceiptEvent {

    private String conversationId;

    private String readerId;

    private Instant readAt;
}