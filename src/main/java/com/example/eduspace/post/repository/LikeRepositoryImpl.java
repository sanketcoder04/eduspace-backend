package com.example.eduspace.post.repository;

import com.example.eduspace.post.entity.Like;
import com.example.eduspace.post.enums.LikeTargetType;
import com.example.eduspace.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Slice<Like> searchPostLikersByName(String postId, String namePart, Pageable pageable) {
        int pageSize = pageable.getPageSize();

        Aggregation aggregation = Aggregation.newAggregation(
                // targetType/targetId/userId are stored as an enum string / ObjectIds. This
                // pipeline is untyped, so the ids must be real ObjectIds to match.
                Aggregation.match(Criteria.where("targetType").is(LikeTargetType.POST.name())
                        .and("targetId").is(new ObjectId(postId))),
                Aggregation.lookup(mongoTemplate.getCollectionName(User.class), "userId", "_id", "user"),
                Aggregation.unwind("user"),
                // Pattern.quote makes the user's text literal, never a regex.
                Aggregation.match(Criteria.where("user.name").regex(Pattern.quote(namePart), "i")),
                // `_id` is the unique tie-breaker that keeps offset paging stable.
                Aggregation.sort(Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("_id"))),
                Aggregation.skip(pageable.getOffset()),
                Aggregation.limit(pageSize + 1L), // one extra row tells us whether another page exists
                Aggregation.project("userId", "createdAt")
        );

        List<Like> rows = mongoTemplate
                .aggregate(aggregation, mongoTemplate.getCollectionName(Like.class), Like.class)
                .getMappedResults();

        boolean hasNext = rows.size() > pageSize;
        return new SliceImpl<>(hasNext ? rows.subList(0, pageSize) : rows, pageable, hasNext);
    }
}