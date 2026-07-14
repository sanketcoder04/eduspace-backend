package com.example.eduspace.auth.service;

import com.example.eduspace.auth.dto.request.*;
import com.example.eduspace.auth.dto.response.AuthResponse;
import com.example.eduspace.auth.dto.response.GenericMessageResponse;
import com.example.eduspace.auth.dto.response.TokenResponse;
import com.example.eduspace.auth.dto.response.UserResponse;
import com.example.eduspace.auth.mapper.AuthMapper;
import com.example.eduspace.auth.validation.AuthValidator;
import com.example.eduspace.common.enums.AuthProvider;
import com.example.eduspace.config.properties.JwtProperties;
import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.mail.service.EmailService;
import com.example.eduspace.refresh.service.RefreshTokenService;
import com.example.eduspace.security.authentication.CustomUserDetails;
import com.example.eduspace.security.jwt.JwtService;
import com.example.eduspace.user.entity.User;
import com.example.eduspace.user.repository.UserRepository;
import com.example.eduspace.verification.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthValidator authValidator;

    private final AuthMapper authMapper;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final VerificationTokenService verificationTokenService;

    private final EmailService emailService;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final JwtProperties jwtProperties;

    public GenericMessageResponse register(RegisterRequest request) {
        authValidator.validateRegistration(request.getEmail());

        User user = authMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProvider(AuthProvider.LOCAL);
        user.setEnabled(false);
        user.setEmailVerified(false);
        user.setAccountLocked(false);

        userRepository.save(user);

        String otp = verificationTokenService.createEmailVerificationToken(user);

        emailService.sendVerificationOtp(user.getEmail(), user.getName(), otp);

        return GenericMessageResponse.builder()
                .message("Registration successful. Please verify your email.")
                .build();

    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        Instant expiresAt = Instant.now().plusMillis(jwtProperties.getRefreshTokenExpiration());

        refreshTokenService.saveRefreshToken(user, refreshToken, expiresAt);

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        UserResponse userResponse = authMapper.toUserResponse(user);

        return AuthResponse.builder()
                .token(tokenResponse)
                .user(userResponse)
                .build();
    }

    public TokenResponse refreshToken(RefreshTokenRequest request) {
        return null;
    }

    public GenericMessageResponse verifyEmail(VerifyEmailRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        authValidator.validateEmailVerification(user);

        verificationTokenService.verifyEmailOtp(user, request.getOtp());

        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(Instant.now());
        userRepository.save(user);

        return GenericMessageResponse
                .builder()
                .message("Email verified successfully.")
                .build();

    }

    public GenericMessageResponse resendOtp(ResendOtpRequest request) {
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        authValidator.validateResendOtp(user);

        String otp = verificationTokenService.createEmailVerificationToken(user);

        emailService.sendVerificationOtp(user.getEmail(), user.getName(), otp);

        return GenericMessageResponse.builder()
                .message("A new verification OTP has been sent to your email.")
                .build();
    }

    public GenericMessageResponse forgotPassword(ForgotPasswordRequest request) {
        return null;
    }

    public GenericMessageResponse resetPassword(ResetPasswordRequest request) {
        return null;
    }

    public GenericMessageResponse logout() {
        return null;
    }
}