package com.example.eduspace.post.controller;

import com.example.eduspace.common.dto.ApiResponse;
import com.example.eduspace.common.dto.SliceResponse;
import com.example.eduspace.post.dto.response.LikerResponse;
import com.example.eduspace.post.enums.LikeTargetType;
import com.example.eduspace.post.service.LikeService;
import com.example.eduspace.security.authentication.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/posts/{id}/like")
    public ResponseEntity<ApiResponse<Boolean>> togglePostLike(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String id) {

        boolean nowLiked = likeService.toggleLike(userDetails.user(), LikeTargetType.POST, id);

        return ResponseEntity.ok(
                ApiResponse.<Boolean>builder().success(true).message("Like toggled.").data(nowLiked).build());
    }

    @PostMapping("/comments/{id}/like")
    public ResponseEntity<ApiResponse<Boolean>> toggleCommentLike(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String id) {

        boolean nowLiked = likeService.toggleLike(userDetails.user(), LikeTargetType.COMMENT, id);

        return ResponseEntity.ok(
                ApiResponse.<Boolean>builder().success(true).message("Like toggled.").data(nowLiked).build());
    }

    @GetMapping("/posts/{id}/likes")
    public ResponseEntity<ApiResponse<SliceResponse<LikerResponse>>> getPostLikers(
            @PathVariable String id,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {

        return ResponseEntity.ok(
                ApiResponse.<SliceResponse<LikerResponse>>builder()
                        .success(true).message("Likes fetched.")
                        .data(likeService.getPostLikers(id, query, page, size)).build());
    }
}