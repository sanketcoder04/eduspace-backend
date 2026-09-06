package com.example.eduspace.follow.service;

import com.example.eduspace.exception.BadRequestException;
import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.follow.entity.Follow;
import com.example.eduspace.follow.entity.FollowStats;
import com.example.eduspace.follow.repository.FollowRepository;
import com.example.eduspace.follow.repository.FollowStatsRepository;
import com.example.eduspace.notification.enums.NotificationType;
import com.example.eduspace.notification.service.NotificationService;
import com.example.eduspace.user.entity.User;
import com.example.eduspace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    private final FollowStatsRepository followStatsRepository;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    @Transactional
    public void follow(User follower, String targetUserId) {
        if (follower.getId().equals(targetUserId)) {
            throw new BadRequestException("You cannot follow yourself.");
        }
        if (!userRepository.existsById(targetUserId)) {
            throw new ResourceNotFoundException("User not found.");
        }
        if (followRepository.findByFollowerIdAndFollowingId(follower.getId(), targetUserId).isPresent()) {
            return; // already following — idempotent, not an error
        }

        followRepository.save(Follow.builder().followerId(follower.getId()).followingId(targetUserId).build());

        adjustStats(follower.getId(), "followingCount", +1);
        adjustStats(targetUserId, "followersCount", +1);

        notificationService.notify(
                targetUserId,
                NotificationType.NEW_FOLLOWER,
                "New follower",
                follower.getName() + " started following you.",
                "PROFILE",
                follower.getId()
        );
    }

    @Transactional
    public void unfollow(User follower, String targetUserId) {
        followRepository.findByFollowerIdAndFollowingId(follower.getId(), targetUserId)
                .ifPresent(follow -> {
                    followRepository.delete(follow);
                    adjustStats(follower.getId(), "followingCount", -1);
                    adjustStats(targetUserId, "followersCount", -1);
                });
    }

    public boolean isFollowing(String followerId, String targetUserId) {
        return followRepository.findByFollowerIdAndFollowingId(followerId, targetUserId).isPresent();
    }

    public FollowStats getStats(String userId) {
        return followStatsRepository.findByUserId(userId)
                .orElseGet(() -> FollowStats.builder().id(userId).userId(userId).build());
    }

    private void adjustStats(String userId, String field, int delta) {
        FollowStats stats = followStatsRepository.findByUserId(userId)
                .orElseGet(() -> FollowStats.builder().id(userId).userId(userId).build());

        if (field.equals("followersCount")) {
            stats.setFollowersCount(Math.max(0, stats.getFollowersCount() + delta));
        }
        else {
            stats.setFollowingCount(Math.max(0, stats.getFollowingCount() + delta));
        }
        followStatsRepository.save(stats);
    }
}