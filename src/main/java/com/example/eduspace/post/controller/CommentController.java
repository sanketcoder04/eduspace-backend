package com.example.eduspace.post.controller;

import com.example.eduspace.common.dto.ApiResponse;
import com.example.eduspace.post.dto.request.CreateCommentRequest;
import com.example.eduspace.post.dto.response.CommentResponse;
import com.example.eduspace.post.service.CommentService;
import com.example.eduspace.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String postId,
            @Valid @RequestBody CreateCommentRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CommentResponse>builder()
                        .success(true).message("Comment added.")
                        .data(commentService.addComment(userDetails.user(), postId, request.getContent(), request.getParentCommentId()))
                        .build());
    }

    @GetMapping("/api/v1/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getTopLevel(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String postId,
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.<Page<CommentResponse>>builder()
                        .success(true).message("Comments fetched.")
                        .data(commentService.getTopLevelComments(userDetails.user(), postId, pageable)).build());
    }

    @GetMapping("/api/v1/comments/{commentId}/replies")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getReplies(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String commentId,
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(
                ApiResponse.<Page<CommentResponse>>builder()
                        .success(true).message("Replies fetched.")
                        .data(commentService.getReplies(userDetails.user(), commentId, pageable)).build());
    }

    @DeleteMapping("/api/v1/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String id) {

        commentService.deleteComment(userDetails.user(), id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Comment deleted.").build());
    }
}