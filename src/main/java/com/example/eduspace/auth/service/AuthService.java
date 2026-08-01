package com.example.eduspace.auth.service;

import com.example.eduspace.auth.dto.request.*;
import com.example.eduspace.auth.dto.response.AuthResponse;
import com.example.eduspace.auth.dto.response.GenericMessageResponse;
import com.example.eduspace.auth.dto.response.TokenResponse;
import com.example.eduspace.auth.dto.response.UserResponse;
import com.example.eduspace.auth.dto.request.VerifyPasswordResetOtpRequest;
import com.example.eduspace.auth.dto.response.VerifyPasswordResetOtpResponse;
import com.example.eduspace.auth.mapper.AuthMapper;
import com.example.eduspace.auth.validation.AuthValidator;
import com.example.eduspace.common.enums.AuthProvider;
import com.example.eduspace.config.properties.JwtProperties;
import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.exception.UnauthorizedException;
import com.example.eduspace.mail.service.EmailService;
import com.example.eduspace.refresh.entity.RefreshToken;
import com.example.eduspace.refresh.service.RefreshTokenService;
import com.example.eduspace.security.authentication.CustomUserDetails;
import com.example.eduspace.security.jwt.JwtService;
import com.example.eduspace.security.jwt.JwtTokenType;
import com.example.eduspace.user.entity.User;
import com.example.eduspace.user.repository.UserRepository;
import com.example.eduspace.verification.service.VerificationTokenService;
import com.example.eduspace.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        RefreshToken stored = refreshTokenService.getRefreshToken(request.getRefreshToken());

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token is invalid or expired.");
        }

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        CustomUserDetails userDetails = new CustomUserDetails(user);

        refreshTokenService.revoke(request.getRefreshToken());

        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);
        Instant expiresAt = Instant.now().plusMillis(jwtProperties.getRefreshTokenExpiration());
        refreshTokenService.saveRefreshToken(user, newRefreshToken, expiresAt);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
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
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    String otp = verificationTokenService.createPasswordResetToken(user);
                    emailService.sendPasswordResetOtp(user.getEmail(), user.getName(), otp);
        });

        return GenericMessageResponse.builder()
                .message("If an account exists with this email, a password reset OTP has been sent.")
                .build();
    }

    @Transactional
    public GenericMessageResponse resetPassword(ResetPasswordRequest request) {
        String resetToken = request.getResetToken();

        String email = jwtService.extractUsername(resetToken);

        if (jwtService.extractTokenType(resetToken) != JwtTokenType.PASSWORD_RESET) {
            throw new BadRequestException("Invalid reset token.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        CustomUserDetails userDetails = new CustomUserDetails(user);

        if (!jwtService.isTokenValid(resetToken, userDetails)) {
            throw new BadRequestException("Invalid or expired reset token.");
        }

        verificationTokenService.verifyPasswordResetToken(user, resetToken);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        refreshTokenService.revokeAll(user);

        // invalidate any existing password reset verification tokens for this user
        verificationTokenService.invalidatePasswordResetTokens(user);

        return GenericMessageResponse.builder()
                .message("Password reset successfully.")
                .build();
    }

    public VerifyPasswordResetOtpResponse verifyPasswordResetOtp(VerifyPasswordResetOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        verificationTokenService.verifyPasswordResetOtp(user, request.getOtp());

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String resetToken = jwtService.generateToken(userDetails, JwtTokenType.PASSWORD_RESET, jwtProperties.getPasswordResetTokenExpiration());

        verificationTokenService.storePasswordResetToken(user, resetToken);

        return VerifyPasswordResetOtpResponse.builder().resetToken(resetToken).build();
    }

    public GenericMessageResponse logout(RefreshTokenRequest request) {
        refreshTokenService.revoke(request.getRefreshToken());
        return GenericMessageResponse.builder().message("Logged out successfully.").build();
    }

    public UserResponse getCurrentUser(User user) {
        return authMapper.toUserResponse(user);
    }
}