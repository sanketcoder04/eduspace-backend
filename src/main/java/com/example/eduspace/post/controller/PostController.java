package com.example.eduspace.post.controller;

import com.example.eduspace.common.dto.ApiResponse;
import com.example.eduspace.post.dto.request.CreatePostRequest;
import com.example.eduspace.post.dto.request.VoteRequest;
import com.example.eduspace.post.dto.response.PostResponse;
import com.example.eduspace.post.service.PostService;
import com.example.eduspace.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreatePostRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<PostResponse>builder()
                        .success(true).message("Post created.")
                        .data(postService.createPost(userDetails.user(), request)).build());
    }

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getFeed(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.<Page<PostResponse>>builder()
                        .success(true).message("Feed fetched.")
                        .data(postService.getFeed(userDetails.user(), pageable)).build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getByUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.<Page<PostResponse>>builder()
                        .success(true).message("Posts fetched.")
                        .data(postService.getPostsByUser(userDetails.user(), userId, pageable)).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getById(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String id) {

        return ResponseEntity.ok(
                ApiResponse.<PostResponse>builder()
                        .success(true).message("Post fetched.")
                        .data(postService.getById(userDetails.user(), id)).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String id) {

        postService.deletePost(userDetails.user(), id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Post deleted.").build());
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<ApiResponse<PostResponse>> vote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody VoteRequest request) {

        return ResponseEntity.ok(
                ApiResponse.<PostResponse>builder()
                        .success(true).message("Vote recorded.")
                        .data(postService.voteOnPoll(userDetails.user(), id, request.getSelectedOptionIds())).build());
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ApiResponse<Long>> getCountByUser(@PathVariable String userId) {
        return ResponseEntity.ok(
                ApiResponse.<Long>builder()
                        .success(true).message("Posts count fetched.")
                        .data(postService.getPostsCount(userId)).build());
    }
}