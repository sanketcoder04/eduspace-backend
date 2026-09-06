package com.example.eduspace.follow.controller;

import com.example.eduspace.common.dto.ApiResponse;
import com.example.eduspace.follow.entity.FollowStats;
import com.example.eduspace.follow.service.FollowService;
import com.example.eduspace.security.authentication.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
}