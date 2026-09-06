package com.example.eduspace.follow.repository;

import com.example.eduspace.follow.entity.FollowStats;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FollowStatsRepository extends MongoRepository<FollowStats, String> {

    Optional<FollowStats> findByUserId(String userId);
}