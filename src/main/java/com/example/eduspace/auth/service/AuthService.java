package com.example.eduspace.auth.service;

import com.example.eduspace.auth.dto.request.*;
import com.example.eduspace.auth.dto.response.*;
import com.example.eduspace.auth.dto.request.VerifyPasswordResetOtpRequest;
import com.example.eduspace.auth.mapper.AuthMapper;
import com.example.eduspace.auth.validation.AuthValidator;
import com.example.eduspace.common.enums.AuthProvider;
import com.example.eduspace.config.properties.JwtProperties;
import com.example.eduspace.exception.ConflictException;
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
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Map;

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

    private final GoogleTokenVerifierService googleTokenVerifierService;

    private static final long GOOGLE_REGISTRATION_TOKEN_EXPIRATION = 10 * 60 * 1000L; // 10 minutes

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

    public GoogleAuthResponse googleAuth(GoogleAuthRequest request) {
        GoogleIdToken.Payload payload = googleTokenVerifierService.verify(request.getIdToken());

        String email = payload.getEmail();
        String googleId = payload.getSubject();
        String name = (String) payload.get("name");

        return userRepository.findByEmail(email)
                .map(user -> loginExistingGoogleUser(user, googleId))
                .orElseGet(() -> requireRoleSelection(email, name, googleId));
    }

    @Transactional
    public AuthResponse completeGoogleRegistration(CompleteGoogleRegistrationRequest request) {
        String token = request.getRegistrationToken();

        if (jwtService.extractTokenType(token) != JwtTokenType.GOOGLE_REGISTRATION) {
            throw new BadRequestException("Invalid registration token.");
        }
        if (jwtService.isTokenExpired(token)) {
            throw new BadRequestException("Registration session has expired. Please try again.");
        }

        String email = jwtService.extractClaimAsString(token, "email");
        String name = jwtService.extractClaimAsString(token, "name");
        String googleId = jwtService.extractClaimAsString(token, "googleId");

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("An account with this email already exists.");
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .provider(AuthProvider.GOOGLE)
                .providerId(googleId)
                .role(request.getRole())
                .enabled(true)
                .emailVerified(true)
                .emailVerifiedAt(Instant.now())
                .accountLocked(false)
                .lastLoginAt(Instant.now())
                .build();

        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        Instant expiresAt = Instant.now().plusMillis(jwtProperties.getRefreshTokenExpiration());
        refreshTokenService.saveRefreshToken(user, refreshToken, expiresAt);

        return AuthResponse.builder()
                .token(TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build())
                .user(authMapper.toUserResponse(user))
                .build();
    }

    private GoogleAuthResponse loginExistingGoogleUser(User user, String googleId) {
        // Google has already verified this email — safe to auto-link and log in directly
        if (user.getProviderId() == null) {
            user.setProviderId(googleId);
        }
        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
            user.setEmailVerifiedAt(Instant.now());
        }
        if (!user.isEnabled()) {
            user.setEnabled(true);
        }
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        Instant expiresAt = Instant.now().plusMillis(jwtProperties.getRefreshTokenExpiration());
        refreshTokenService.saveRefreshToken(user, refreshToken, expiresAt);

        AuthResponse authResponse = AuthResponse.builder()
                .token(TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build())
                .user(authMapper.toUserResponse(user))
                .build();

        return GoogleAuthResponse.builder()
                .status(GoogleAuthStatus.LOGIN_SUCCESS)
                .auth(authResponse)
                .build();
    }

    private GoogleAuthResponse requireRoleSelection(String email, String name, String googleId) {
        Map<String, Object> claims = Map.of("email", email, "name", name, "googleId", googleId);

        String registrationToken = jwtService.generateClaimsOnlyToken(
                claims, email, JwtTokenType.GOOGLE_REGISTRATION, GOOGLE_REGISTRATION_TOKEN_EXPIRATION);

        return GoogleAuthResponse.builder()
                .status(GoogleAuthStatus.REGISTRATION_REQUIRED)
                .registrationToken(registrationToken)
                .email(email)
                .name(name)
                .build();
    }
}