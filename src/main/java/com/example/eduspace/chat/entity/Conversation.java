package com.example.eduspace.chat.entity;

import com.example.eduspace.chat.enums.ConversationStatus;
import com.example.eduspace.common.entity.BaseEntity;
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
@Document(collection = "conversations")
public class Conversation extends BaseEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field(targetType = FieldType.OBJECT_ID)
    private String applicationId; // 1:1 with an approved Application

    @Field(targetType = FieldType.OBJECT_ID)
    private String opportunityId;

    @Field(targetType = FieldType.OBJECT_ID)
    private String authorId;

    @Field(targetType = FieldType.OBJECT_ID)
    private String applicantId;

    private ConversationStatus status;

    private String lastMessagePreview;

    private Instant lastMessageAt;
}