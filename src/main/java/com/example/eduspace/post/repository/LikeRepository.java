package com.example.eduspace.post.repository;

import com.example.eduspace.post.entity.Like;
import com.example.eduspace.post.enums.LikeTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends MongoRepository<Like, String> {

    Optional<Like> findByTargetTypeAndTargetIdAndUserId(LikeTargetType type, String targetId, String userId);

    Page<Like> findByTargetTypeAndTargetId(LikeTargetType type, String targetId, Pageable pageable);

    List<Like> findByTargetTypeAndTargetIdInAndUserId(LikeTargetType type, List<String> targetIds, String userId);
}