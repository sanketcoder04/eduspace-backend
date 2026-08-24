package com.example.eduspace.chat.entity;

import com.example.eduspace.chat.enums.MessageType;
import com.example.eduspace.common.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "messages")
public class Message extends BaseEntity {

    @Id
    private String id;

    @Indexed
    private String conversationId;

    private String senderId; // null for SYSTEM messages

    private MessageType type;

    private String content;

    @Builder.Default
    private boolean read = false;
}