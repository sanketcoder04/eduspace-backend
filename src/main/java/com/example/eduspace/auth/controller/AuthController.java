package com.example.eduspace.auth.controller;

import com.example.eduspace.auth.dto.request.*;
import com.example.eduspace.auth.dto.request.VerifyPasswordResetOtpRequest;
import com.example.eduspace.auth.dto.response.AuthResponse;
import com.example.eduspace.auth.dto.response.GenericMessageResponse;
import com.example.eduspace.auth.dto.response.VerifyPasswordResetOtpResponse;
import com.example.eduspace.auth.service.AuthService;
import com.example.eduspace.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<GenericMessageResponse>> register(@Valid @RequestBody RegisterRequest request) {
        GenericMessageResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<GenericMessageResponse>builder()
                                .success(true)
                                .message(response.getMessage())
                                .data(response)
                                .build()
                );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<GenericMessageResponse>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        GenericMessageResponse response = authService.verifyEmail(request);

        return ResponseEntity.ok(
                ApiResponse.<GenericMessageResponse>builder()
                        .success(true)
                        .message(response.getMessage())
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<GenericMessageResponse>> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        GenericMessageResponse response = authService.resendOtp(request);

        return ResponseEntity.ok(
                ApiResponse.<GenericMessageResponse>builder()
                        .success(true)
                        .message(response.getMessage())
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Login successful.")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<GenericMessageResponse>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        GenericMessageResponse response = authService.forgotPassword(request);

        return ResponseEntity.ok(
                ApiResponse.<GenericMessageResponse>builder()
                        .success(true)
                        .message(response.getMessage())
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/verify-password-reset-otp")
    public ResponseEntity<ApiResponse<VerifyPasswordResetOtpResponse>> verifyPasswordResetOtp(@Valid @RequestBody VerifyPasswordResetOtpRequest request) {
        VerifyPasswordResetOtpResponse response = authService.verifyPasswordResetOtp(request);

        return ResponseEntity.ok(
                ApiResponse.<VerifyPasswordResetOtpResponse>builder()
                        .success(true)
                        .message("OTP verified successfully.")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<GenericMessageResponse>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        GenericMessageResponse response = authService.resetPassword(request);

        return ResponseEntity.ok(
                ApiResponse.<GenericMessageResponse>builder()
                        .success(true)
                        .message(response.getMessage())
                        .data(response)
                        .build()
        );
    }
}