package com.example.eduspace.post.repository;

import com.example.eduspace.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {

    Page<Post> findByAuthorId(String authorId, Pageable pageable);

    Page<Post> findByAuthorIdIn(List<String> authorIds, Pageable pageable);

    Page<Post> findByMentionsContaining(String userId, Pageable pageable);
}