package com.example.eduspace.notification.controller;

import com.example.eduspace.common.dto.ApiResponse;
import com.example.eduspace.notification.dto.response.NotificationResponse;
import com.example.eduspace.notification.service.NotificationService;
import com.example.eduspace.security.authentication.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getMine(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<NotificationResponse> notifications = notificationService.getForUser(userDetails.user(), pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<NotificationResponse>>builder()
                        .success(true)
                        .message("Notifications fetched.")
                        .data(notifications)
                        .build()
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> unreadCount(@AuthenticationPrincipal CustomUserDetails userDetails) {

        Long unreadCount = notificationService.getUnreadCount(userDetails.user());

        return ResponseEntity.ok(
                ApiResponse.<Long>builder()
                        .success(true)
                        .message("Unread count fetched.")
                        .data(unreadCount)
                        .build()
        );
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markRead(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String id) {

        NotificationResponse markAsRead = notificationService.markRead(userDetails.user(), id);

        return ResponseEntity.ok(
                ApiResponse.<NotificationResponse>builder()
                        .success(true)
                        .message("Notification marked read.")
                        .data(markAsRead)
                        .build()
        );
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead(@AuthenticationPrincipal CustomUserDetails userDetails) {

        notificationService.markAllRead(userDetails.user());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("All notifications marked read.")
                        .build()
        );
    }
}