package com.example.eduspace.post.repository;

import com.example.eduspace.post.entity.PollVote;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PollVoteRepository extends MongoRepository<PollVote, String> {

    Optional<PollVote> findByPostIdAndUserId(String postId, String userId);
}