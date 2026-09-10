package com.example.eduspace.post.repository;

import com.example.eduspace.post.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<Comment, String> {

    Page<Comment> findByPostIdAndParentCommentIdIsNullOrderByCreatedAtDesc(String postId, Pageable pageable);

    Page<Comment> findByParentCommentIdOrderByCreatedAtAsc(String parentCommentId, Pageable pageable);

    long countByPostId(String postId);

    long countByParentCommentId(String parentCommentId);
}