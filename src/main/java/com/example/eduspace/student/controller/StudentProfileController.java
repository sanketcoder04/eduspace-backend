package com.example.eduspace.student.controller;

import com.example.eduspace.common.dto.*;
import com.example.eduspace.security.authentication.CustomUserDetails;
import com.example.eduspace.student.dto.request.UpdateStudentBasicInfoRequest;
import com.example.eduspace.student.dto.response.StudentProfileResponse;
import com.example.eduspace.student.service.StudentProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile/student")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        StudentProfileResponse response = studentProfileService.getMyProfile(userDetails.user());

        return ResponseEntity.ok(
                ApiResponse.<StudentProfileResponse>builder()
                        .success(true)
                        .message("Profile fetched.")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/basic-info")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> updateBasicInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateStudentBasicInfoRequest request) {

        StudentProfileResponse response =
                studentProfileService.updateBasicInfo(userDetails.user(), request);

        return ResponseEntity.ok(
                ApiResponse.<StudentProfileResponse>builder()
                        .success(true)
                        .message("Basic info updated.")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/education")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> updateEducation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateEducationListRequest request) {

        StudentProfileResponse response =
                studentProfileService.updateEducation(userDetails.user(), request.getEducation());

        return ResponseEntity.ok(
                ApiResponse.<StudentProfileResponse>builder()
                        .success(true)
                        .message("Education details updated.")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/verification")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> submitVerification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SubmitVerificationRequest request) {

        StudentProfileResponse response =
                studentProfileService.submitVerification(userDetails.user(), request);

        return ResponseEntity.ok(
                ApiResponse.<StudentProfileResponse>builder()
                        .success(true)
                        .message("Verification submitted.")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/avatar")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> updateAvatar(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateImageRequest request) {

        StudentProfileResponse response = studentProfileService.updateAvatar(userDetails.user(), request.getUrl());

        return ResponseEntity.ok(
                ApiResponse.<StudentProfileResponse>builder()
                        .success(true)
                        .message("Profile photo updated.")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/cover")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> updateCover(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateImageRequest request) {

        StudentProfileResponse response = studentProfileService.updateCover(userDetails.user(), request.getUrl());

        return ResponseEntity.ok(
                ApiResponse.<StudentProfileResponse>builder()
                        .success(true)
                        .message("Cover photo updated.")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/certificates")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> addCertificate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddCertificateRequest request) {

        StudentProfileResponse response = studentProfileService.addCertificate(userDetails.user(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<StudentProfileResponse>builder()
                        .success(true)
                        .message("Certificate added.")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/certificates/{certificateId}")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> updateCertificate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String certificateId,
            @Valid @RequestBody UpdateCertificateRequest request) {

        StudentProfileResponse response =
                studentProfileService.updateCertificate(userDetails.user(), certificateId, request);

        return ResponseEntity.ok(
                ApiResponse.<StudentProfileResponse>builder()
                        .success(true)
                        .message("Certificate updated.")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/certificates/{certificateId}")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> deleteCertificate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String certificateId) {

        StudentProfileResponse response = studentProfileService.deleteCertificate(userDetails.user(), certificateId);

        return ResponseEntity.ok(
                ApiResponse.<StudentProfileResponse>builder()
                        .success(true)
                        .message("Certificate removed.")
                        .data(response)
                        .build()
        );
    }
}