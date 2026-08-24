package com.example.eduspace.notification.dto.response;

import com.example.eduspace.notification.enums.NotificationType;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private String id;

    private NotificationType type;

    private String title;

    private String body;

    private String referenceType;

    private String referenceId;

    private boolean read;

    private Instant readAt;

    private Instant createdAt;
}