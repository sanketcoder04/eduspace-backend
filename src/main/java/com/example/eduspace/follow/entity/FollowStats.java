package com.example.eduspace.follow.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

/**
 * Denormalized counters, one document per user — updated on every
 * follow/unfollow rather than counted live, since "followers/following
 * count" is read constantly (every profile view) but written rarely.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "follow_stats")
public class FollowStats {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field(targetType = FieldType.OBJECT_ID)
    private String userId;

    @Builder.Default
    private int followersCount = 0;

    @Builder.Default
    private int followingCount = 0;
}