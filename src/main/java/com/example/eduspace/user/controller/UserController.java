package com.example.eduspace.user.controller;

import com.example.eduspace.common.dto.ApiResponse;
import com.example.eduspace.security.authentication.CustomUserDetails;
import com.example.eduspace.user.dto.response.UserSummaryResponse;
import com.example.eduspace.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/summary")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> getSummary(@PathVariable String userId) {
        return ResponseEntity.ok(
                ApiResponse.<UserSummaryResponse>builder()
                        .success(true).message("User summary fetched.")
                        .data(userService.getSummary(userId)).build());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> search(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String query) {

        return ResponseEntity.ok(
                ApiResponse.<List<UserSummaryResponse>>builder()
                        .success(true).message("Search results fetched.")
                        .data(userService.searchUsers(query, userDetails.user().getId())).build());
    }
}