package com.example.eduspace.post.entity;

import com.example.eduspace.common.entity.BaseEntity;
import com.example.eduspace.common.enums.Role;
import com.example.eduspace.post.enums.PostType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
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
@Document(collection = "posts")
@CompoundIndex(name = "author_feed", def = "{'authorId': 1, 'createdAt': -1}")
public class Post extends BaseEntity {

    @Id
    private String id;

    @Field(targetType = FieldType.OBJECT_ID)
    @Indexed
    private String authorId;

    private Role authorRole;

    private PostType type;

    private String content;

    private List<String> mediaUrls;

    private String documentUrl;

    private String documentFileName;

    private PollData poll;

    @Builder.Default
    private List<String> mentions = List.of();

    @Builder.Default
    private int likesCount = 0;

    @Builder.Default
    private int commentsCount = 0;

    @Builder.Default
    private boolean edited = false;
}