package com.example.eduspace.post.entity;

import com.example.eduspace.common.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
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
@Document(collection = "poll_votes")
@CompoundIndex(name = "unique_vote", def = "{'postId': 1, 'userId': 1}", unique = true)
public class PollVote extends BaseEntity {

    @Id
    private String id;

    @Field(targetType = FieldType.OBJECT_ID)
    private String postId;

    @Field(targetType = FieldType.OBJECT_ID)
    private String userId;

    private List<String> selectedOptionIds;
}