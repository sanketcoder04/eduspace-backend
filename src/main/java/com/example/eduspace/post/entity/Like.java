package com.example.eduspace.post.entity;

import com.example.eduspace.common.entity.BaseEntity;
import com.example.eduspace.post.enums.LikeTargetType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "likes")
@CompoundIndex(name = "unique_like", def = "{'targetType': 1, 'targetId': 1, 'userId': 1}", unique = true)
public class Like extends BaseEntity {

    @Id
    private String id;

    private LikeTargetType targetType;

    @Field(targetType = FieldType.OBJECT_ID)
    private String targetId;

    @Field(targetType = FieldType.OBJECT_ID)
    private String userId;
}