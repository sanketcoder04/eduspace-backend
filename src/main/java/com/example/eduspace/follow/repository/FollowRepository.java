package com.example.eduspace.follow.repository;

import com.example.eduspace.follow.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends MongoRepository<Follow, String> {

    Optional<Follow> findByFollowerIdAndFollowingId(String followerId, String followingId);

    Page<Follow> findByFollowingId(String followingId, Pageable pageable);

    Page<Follow> findByFollowerId(String followerId, Pageable pageable);

    List<Follow> findByFollowerId(String followerId); // unpaginated — used to build the feed's author list

    void deleteByFollowerIdAndFollowingId(String followerId, String followingId);
}