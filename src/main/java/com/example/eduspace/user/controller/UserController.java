package com.example.eduspace.user.controller;

import com.example.eduspace.common.dto.ApiResponse;
import com.example.eduspace.user.dto.response.UserSummaryResponse;
import com.example.eduspace.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}