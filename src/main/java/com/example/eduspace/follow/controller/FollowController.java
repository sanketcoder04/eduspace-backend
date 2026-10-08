package com.example.eduspace.follow.controller;

import com.example.eduspace.common.dto.ApiResponse;
import com.example.eduspace.exception.ForbiddenException;
import com.example.eduspace.follow.dto.response.FollowedUserResponse;
import com.example.eduspace.follow.dto.response.RecommendedProfileResponse;
import com.example.eduspace.follow.entity.FollowStats;
import com.example.eduspace.follow.service.FollowService;
import com.example.eduspace.security.authentication.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> follow(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String userId) {

        followService.follow(userDetails.user(), userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Now following.").build());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String userId) {

        followService.unfollow(userDetails.user(), userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Unfollowed.").build());
    }

    @GetMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<Boolean>> status(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String userId) {

        return ResponseEntity.ok(
                ApiResponse.<Boolean>builder().success(true).message("Status fetched.")
                        .data(followService.isFollowing(userDetails.user().getId(), userId)).build());
    }

    @GetMapping("/{userId}/stats")
    public ResponseEntity<ApiResponse<FollowStats>> stats(@PathVariable String userId) {
        return ResponseEntity.ok(
                ApiResponse.<FollowStats>builder().success(true).message("Stats fetched.")
                        .data(followService.getStats(userId)).build());
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponse<Page<FollowedUserResponse>>> getFollowers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String userId,
            @PageableDefault(size = 20) Pageable pageable) {

        if (!userDetails.user().getId().equals(userId)) {
            throw new ForbiddenException("You can only view your own followers list.");
        }

        return ResponseEntity.ok(
                ApiResponse.<Page<FollowedUserResponse>>builder()
                        .success(true).message("Followers fetched.")
                        .data(followService.getFollowers(userId, pageable)).build());
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<ApiResponse<Page<FollowedUserResponse>>> getFollowing(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String userId,
            @PageableDefault(size = 20) Pageable pageable) {

        if (!userDetails.user().getId().equals(userId)) {
            throw new ForbiddenException("You can only view your own following list.");
        }

        return ResponseEntity.ok(
                ApiResponse.<Page<FollowedUserResponse>>builder()
                        .success(true).message("Following fetched.")
                        .data(followService.getFollowing(userId, pageable)).build());
    }

    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<List<RecommendedProfileResponse>>> getRecommendations(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "5") int limit) {

        return ResponseEntity.ok(
                ApiResponse.<List<RecommendedProfileResponse>>builder()
                        .success(true).message("Recommendations fetched.")
                        .data(followService.getRecommendations(userDetails.user(), limit)).build());
    }
}