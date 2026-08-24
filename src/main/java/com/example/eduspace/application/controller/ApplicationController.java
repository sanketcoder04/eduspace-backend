package com.example.eduspace.application.controller;

import com.example.eduspace.application.dto.request.*;
import com.example.eduspace.application.dto.response.ApplicationResponse;
import com.example.eduspace.application.service.ApplicationService;
import com.example.eduspace.common.dto.ApiResponse;
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
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationResponse>> apply(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateApplicationRequest request) {

        ApplicationResponse created = applicationService.apply(userDetails.user(), request.getOpportunityId(), request.getMessage());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ApplicationResponse>builder()
                        .success(true)
                        .message("Application submitted.")
                        .data(created)
                        .build()
        );
    }

    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<Page<ApplicationResponse>>> getSent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ApplicationResponse> sentApplications = applicationService.getSentApplications(userDetails.user(), pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<ApplicationResponse>>builder()
                        .success(true)
                        .message("Applications fetched.")
                        .data(sentApplications)
                        .build()
        );
    }

    @GetMapping("/received")
    public ResponseEntity<ApiResponse<Page<ApplicationResponse>>> getReceived(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ApplicationResponse> receivedApplications = applicationService.getReceivedApplications(userDetails.user(), pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<ApplicationResponse>>builder()
                        .success(true)
                        .message("Applications fetched.")
                        .data(receivedApplications)
                        .build()
        );
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<ApplicationResponse>> approve(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String id) {

        ApplicationResponse approved = applicationService.approve(userDetails.user(), id);

        return ResponseEntity.ok(
                ApiResponse.<ApplicationResponse>builder()
                        .success(true)
                        .message("Application approved.")
                        .data(approved)
                        .build()
        );
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<ApplicationResponse>> reject(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @RequestBody(required = false) RejectApplicationRequest request) {

        String reason = request != null ? request.getReason() : null;

        ApplicationResponse rejected = applicationService.reject(userDetails.user(), id, reason);

        return ResponseEntity.ok(
                ApiResponse.<ApplicationResponse>builder()
                        .success(true)
                        .message("Application rejected.")
                        .data(rejected)
                        .build()
        );
    }

    @PatchMapping("/{id}/withdraw")
    public ResponseEntity<ApiResponse<ApplicationResponse>> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String id) {

        ApplicationResponse withdrawn = applicationService.withdraw(userDetails.user(), id);

        return ResponseEntity.ok(
                ApiResponse.<ApplicationResponse>builder()
                        .success(true)
                        .message("Application withdrawn.")
                        .data(withdrawn)
                        .build()
        );
    }

    @PatchMapping("/{id}/contact-consent")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateContactConsent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody ContactShareConsentRequest request) {

        ApplicationResponse updateConsent = applicationService.updateContactConsent(userDetails.user(), id, request);

        return ResponseEntity.ok(
                ApiResponse.<ApplicationResponse>builder()
                        .success(true)
                        .message("Contact sharing preference updated.")
                        .data(updateConsent)
                        .build()
        );
    }
}