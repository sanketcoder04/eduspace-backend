package com.example.eduspace.post.entity;

import com.example.eduspace.common.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "comments")
public class Comment extends BaseEntity {

    @Id
    private String id;

    @Indexed
    @Field(targetType = FieldType.OBJECT_ID)
    private String postId;

    @Field(targetType = FieldType.OBJECT_ID)
    private String authorId;

    private String content;

    @Builder.Default
    private List<String> mentions = List.of();

    @Field(targetType = FieldType.OBJECT_ID)
    private String parentCommentId;

    @Builder.Default
    private int likesCount = 0;

    @Builder.Default
    private boolean edited = false;
}