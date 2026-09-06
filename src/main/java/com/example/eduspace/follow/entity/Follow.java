package com.example.eduspace.follow.entity;

import com.example.eduspace.common.entity.BaseEntity;
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
@Document(collection = "follows")
@CompoundIndex(name = "unique_follow", def = "{'followerId': 1, 'followingId': 1}", unique = true)
public class Follow extends BaseEntity {

    @Id
    private String id;

    @Field(targetType = FieldType.OBJECT_ID)
    private String followerId;

    @Field(targetType = FieldType.OBJECT_ID)
    private String followingId;
}