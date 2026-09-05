package com.example.eduspace.opportunity.controller;

import com.example.eduspace.common.dto.ApiResponse;
import com.example.eduspace.opportunity.dto.request.*;
import com.example.eduspace.opportunity.dto.response.OpportunityResponse;
import com.example.eduspace.opportunity.service.OpportunityService;
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
@RequestMapping("/api/v1/opportunities")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService opportunityService;

    @PostMapping("/teaching-openings")
    public ResponseEntity<ApiResponse<OpportunityResponse>> createTeachingOpening(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateTeachingOpeningRequest request) {

        OpportunityResponse created = opportunityService.createTeachingOpening(userDetails.user(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<OpportunityResponse>builder()
                        .success(true)
                        .message("Teaching opening posted.")
                        .data(created)
                        .build()
        );
    }

    @PostMapping("/tuition-requirements")
    public ResponseEntity<ApiResponse<OpportunityResponse>> createTuitionRequirement(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateTuitionRequirementRequest request) {

        OpportunityResponse created = opportunityService.createTuitionRequirement(userDetails.user(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<OpportunityResponse>builder()
                        .success(true)
                        .message("Tuition requirement posted.")
                        .data(created)
                        .build()
        );
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<OpportunityResponse>>> search(
            @RequestBody OpportunityFilterRequest filter,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<OpportunityResponse> results = opportunityService.search(filter, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<OpportunityResponse>>builder()
                        .success(true)
                        .message("Opportunities fetched.")
                        .data(results)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OpportunityResponse>> getById(@PathVariable String id) {
        OpportunityResponse opportunity = opportunityService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<OpportunityResponse>builder()
                        .success(true)
                        .message("Opportunity fetched.")
                        .data(opportunity)
                        .build()
        );
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<ApiResponse<OpportunityResponse>> close(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String id) {

        OpportunityResponse updated = opportunityService.close(userDetails.user(), id);

        return ResponseEntity.ok(
                ApiResponse.<OpportunityResponse>builder()
                        .success(true)
                        .message("Opportunity closed.")
                        .data(updated)
                        .build()
        );
    }

    @PatchMapping("/{id}/reopen")
    public ResponseEntity<ApiResponse<OpportunityResponse>> reopen(
            @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String id) {

        OpportunityResponse updated = opportunityService.reopen(userDetails.user(), id);

        return ResponseEntity.ok(
                ApiResponse.<OpportunityResponse>builder()
                        .success(true)
                        .message("Opportunity reopened.")
                        .data(updated)
                        .build()
        );
    }
}