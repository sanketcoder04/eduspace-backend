package com.example.eduspace.teacher.controller;

import com.example.eduspace.common.dto.ApiResponse;
import com.example.eduspace.common.dto.UpdateEducationListRequest;
import com.example.eduspace.security.authentication.CustomUserDetails;
import com.example.eduspace.teacher.dto.request.AddSubjectOfferingRequest;
import com.example.eduspace.teacher.dto.request.SubmitVerificationRequest;
import com.example.eduspace.teacher.dto.request.UpdateSubjectOfferingRequest;
import com.example.eduspace.teacher.dto.request.UpdateTeacherBasicInfoRequest;
import com.example.eduspace.teacher.dto.response.TeacherProfileResponse;
import com.example.eduspace.teacher.service.TeacherProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile/teacher")
@RequiredArgsConstructor
public class TeacherProfileController {

    private final TeacherProfileService teacherProfileService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<TeacherProfileResponse>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        TeacherProfileResponse response = teacherProfileService.getMyProfile(userDetails.user());

        return ResponseEntity.ok(
                ApiResponse.<TeacherProfileResponse>builder()
                        .success(true)
                        .message("Profile fetched.")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/basic-info")
    public ResponseEntity<ApiResponse<TeacherProfileResponse>> updateBasicInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateTeacherBasicInfoRequest request) {

        TeacherProfileResponse response =
                teacherProfileService.updateBasicInfo(userDetails.user(), request);

        return ResponseEntity.ok(
                ApiResponse.<TeacherProfileResponse>builder()
                        .success(true)
                        .message("Basic info updated.")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/education")
    public ResponseEntity<ApiResponse<TeacherProfileResponse>> updateEducation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateEducationListRequest request) {

        TeacherProfileResponse response =
                teacherProfileService.updateEducation(userDetails.user(), request.getEducation());

        return ResponseEntity.ok(
                ApiResponse.<TeacherProfileResponse>builder()
                        .success(true)
                        .message("Education details updated.")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/subjects")
    public ResponseEntity<ApiResponse<TeacherProfileResponse>> addSubjectOffering(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddSubjectOfferingRequest request) {

        TeacherProfileResponse response =
                teacherProfileService.addSubjectOffering(userDetails.user(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<TeacherProfileResponse>builder()
                        .success(true)
                        .message("Subject offering added.")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/subjects/{subjectId}")
    public ResponseEntity<ApiResponse<TeacherProfileResponse>> updateSubjectOffering(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String subjectId,
            @Valid @RequestBody UpdateSubjectOfferingRequest request) {

        TeacherProfileResponse response =
                teacherProfileService.updateSubjectOffering(userDetails.user(), subjectId, request);

        return ResponseEntity.ok(
                ApiResponse.<TeacherProfileResponse>builder()
                        .success(true)
                        .message("Subject offering updated.")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/subjects/{subjectId}")
    public ResponseEntity<ApiResponse<TeacherProfileResponse>> deleteSubjectOffering(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String subjectId) {

        TeacherProfileResponse response =
                teacherProfileService.deleteSubjectOffering(userDetails.user(), subjectId);

        return ResponseEntity.ok(
                ApiResponse.<TeacherProfileResponse>builder()
                        .success(true)
                        .message("Subject offering removed.")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/verification")
    public ResponseEntity<ApiResponse<TeacherProfileResponse>> submitVerification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SubmitVerificationRequest request) {

        TeacherProfileResponse response =
                teacherProfileService.submitVerification(userDetails.user(), request);

        return ResponseEntity.ok(
                ApiResponse.<TeacherProfileResponse>builder()
                        .success(true)
                        .message("Verification submitted.")
                        .data(response)
                        .build()
        );
    }
}