package com.example.eduspace.follow.service;

import com.example.eduspace.common.dto.SliceResponse;
import com.example.eduspace.common.service.ProfileLookupService;
import com.example.eduspace.exception.BadRequestException;
import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.follow.dto.response.DiscoverableUserResponse;
import com.example.eduspace.follow.dto.response.FollowedUserResponse;
import com.example.eduspace.follow.dto.response.RecommendedProfileResponse;
import com.example.eduspace.follow.entity.Follow;
import com.example.eduspace.follow.entity.FollowStats;
import com.example.eduspace.follow.repository.FollowRepository;
import com.example.eduspace.follow.repository.FollowStatsRepository;
import com.example.eduspace.notification.enums.NotificationType;
import com.example.eduspace.notification.service.NotificationService;
import com.example.eduspace.user.dto.response.UserSummaryResponse;
import com.example.eduspace.user.entity.User;
import com.example.eduspace.user.repository.UserRepository;
import com.example.eduspace.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    private final FollowStatsRepository followStatsRepository;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    private final ProfileLookupService profileLookupService;

    private final UserService userService;

    private static final int DISCOVER_MIN_QUERY_LENGTH = 2;
    private static final int DISCOVER_MAX_QUERY_LENGTH = 100;
    private static final int DISCOVER_DEFAULT_PAGE_SIZE = 8;
    private static final int DISCOVER_MAX_PAGE_SIZE = 20;

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

    public Page<FollowedUserResponse> getFollowers(String userId, Pageable pageable) {
        return followRepository.findByFollowingId(userId, pageable)
                .map(follow -> toFollowedUserResponse(follow.getFollowerId(), follow.getCreatedAt()));
    }

    public Page<FollowedUserResponse> getFollowing(String userId, Pageable pageable) {
        return followRepository.findByFollowerId(userId, pageable)
                .map(follow -> toFollowedUserResponse(follow.getFollowingId(), follow.getCreatedAt()));
    }

    public List<RecommendedProfileResponse> getRecommendations(User viewer, int limit) {
        List<String> alreadyFollowing = followRepository.findByFollowerId(viewer.getId()).stream()
                .map(Follow::getFollowingId)
                .collect(Collectors.toList());
        alreadyFollowing.add(viewer.getId());

        return userRepository
                .findByIdNotIn(alreadyFollowing, PageRequest.of(0, limit, Sort.by("createdAt").descending()))
                .stream()
                .map(user -> {
                    var summary = profileLookupService.getSummary(user.getId());
                    return RecommendedProfileResponse.builder()
                            .userId(user.getId())
                            .name(summary.name())
                            .avatarUrl(summary.avatarUrl())
                            .role(user.getRole())
                            .build();
                })
                .toList();
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

    private FollowedUserResponse toFollowedUserResponse(String otherUserId, java.time.Instant followedAt) {
        var summary = profileLookupService.getSummary(otherUserId);
        var role = userRepository.findById(otherUserId).map(User::getRole).orElse(null);

        return FollowedUserResponse.builder()
                .userId(otherUserId)
                .name(summary.name())
                .avatarUrl(summary.avatarUrl())
                .role(role)
                .followedAt(followedAt)
                .build();
    }

    public SliceResponse<DiscoverableUserResponse> discover(User viewer, String query, int page, Integer requestedSize) {
        String trimmed = query == null ? "" : query.trim();

        if (trimmed.length() > DISCOVER_MAX_QUERY_LENGTH) {
            throw new BadRequestException("Search text is too long.");
        }

        int size = requestedSize == null
                ? DISCOVER_DEFAULT_PAGE_SIZE
                : Math.min(Math.max(requestedSize, 1), DISCOVER_MAX_PAGE_SIZE);

        int safePage = Math.max(page, 0);

        if (trimmed.length() < DISCOVER_MIN_QUERY_LENGTH) {
            return SliceResponse.empty(safePage, size);
        }

        Slice<UserSummaryResponse> matches = userService.searchUsersPage(trimmed, viewer.getId(), safePage, size);

        if (!matches.hasContent()) {
            return new SliceResponse<>(List.of(), safePage, size, matches.hasNext());
        }

        Set<String> followedIds = followRepository
                .findByFollowerIdAndFollowingIdIn(viewer.getId(), matches.getContent().stream().map(UserSummaryResponse::getId).toList())
                .stream()
                .map(Follow::getFollowingId)
                .collect(Collectors.toSet());

        List<DiscoverableUserResponse> content = matches.getContent().stream()
                .map(match -> DiscoverableUserResponse.builder()
                        .userId(match.getId())
                        .name(match.getName())
                        .avatarUrl(match.getAvatarUrl())
                        .role(match.getRole())
                        .following(followedIds.contains(match.getId()))
                        .build())
                .toList();

        return new SliceResponse<>(content, safePage, size, matches.hasNext());
    }
}