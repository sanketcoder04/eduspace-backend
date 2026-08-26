package com.example.eduspace.notification.entity;

import com.example.eduspace.common.entity.BaseEntity;
import com.example.eduspace.notification.enums.NotificationType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "notifications")
public class Notification extends BaseEntity {

    @Id
    private String id;

    @Indexed
    @Field(targetType = FieldType.OBJECT_ID)
    private String recipientId;

    private NotificationType type;

    private String title;

    private String body;

    private String referenceType; // "OPPORTUNITY" | "APPLICATION" | "CONVERSATION"

    @Field(targetType = FieldType.OBJECT_ID)
    private String referenceId;

    @Indexed
    @Builder.Default
    private boolean read = false;

    private Instant readAt;
}