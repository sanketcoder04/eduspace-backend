package com.example.eduspace.verification.service;

import com.example.eduspace.common.util.OtpUtil;
import com.example.eduspace.exception.BadRequestException;
import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.user.entity.User;
import com.example.eduspace.verification.entity.VerificationToken;
import com.example.eduspace.verification.enums.VerificationType;
import com.example.eduspace.verification.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository repository;

    private final PasswordEncoder passwordEncoder;

    public String createEmailVerificationToken(User user) {
        return createToken(user, VerificationType.EMAIL_VERIFICATION);
    }

    public String createPasswordResetToken(User user) {
        return createToken(user, VerificationType.PASSWORD_RESET);
    }

    public VerificationToken getEmailVerificationToken(User user) {
        return repository.findByUserIdAndTypeAndUsedFalse(user.getId(), VerificationType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new ResourceNotFoundException("Verification token not found."));
    }

    public void verifyEmailOtp(User user, String otp) {
        verifyToken(user, otp, VerificationType.EMAIL_VERIFICATION);
    }

    public VerificationToken verifyPasswordResetOtp(User user, String otp) {
        return verifyToken(user, otp, VerificationType.PASSWORD_RESET, false);
    }

    private String createToken(User user, VerificationType type) {
        // Invalidate any existing unused tokens of this type for this user
        repository.findByUserIdAndTypeAndUsedFalse(user.getId(), type)
                .ifPresent(existing -> {
                    existing.setUsed(true);
                    existing.setUsedAt(Instant.now());
                    repository.save(existing);
                });

        String otp = OtpUtil.generateOtp();

        VerificationToken verificationToken =
                VerificationToken.builder()
                        .userId(user.getId())
                        .token(passwordEncoder.encode(otp))
                        .type(type)
                        .expiresAt(Instant.now().plus(10, ChronoUnit.MINUTES))
                        .used(false)
                        .build();

        repository.save(verificationToken);
        return otp;
    }

    private VerificationToken verifyToken(User user, String otp, VerificationType type) {
        return verifyToken(user, otp, type, true);
    }

    private VerificationToken verifyToken(User user, String otp, VerificationType type, boolean markAsUsed) {
        VerificationToken verificationToken = repository
                        .findByUserIdAndTypeAndUsedFalse(user.getId(), type)
                        .orElseThrow(() -> new ResourceNotFoundException("Verification token not found."));

        if (verificationToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("OTP has expired.");
        }

        if (!passwordEncoder.matches(otp, verificationToken.getToken())) {
            throw new BadRequestException("Invalid OTP.");
        }

        if (markAsUsed) {
            verificationToken.setUsed(true);
            verificationToken.setUsedAt(Instant.now());

            repository.save(verificationToken);
        }

        return verificationToken;
    }

    public void invalidatePasswordResetTokens(User user) {
        // Don't delete records - keep for audit trail
        // Records are already marked as used when password reset is completed
    }

    public void storePasswordResetToken(User user, String resetToken) {
        VerificationToken verificationToken = repository
                .findByUserIdAndTypeAndUsedFalse(user.getId(), VerificationType.PASSWORD_RESET)
                .orElseThrow(() -> new ResourceNotFoundException("Password reset verification not found."));

        // Store the JWT reset token (unencrypted since JWTs have built-in signature verification)
        verificationToken.setToken(resetToken);
        verificationToken.setUsed(false);
        verificationToken.setExpiresAt(Instant.now().plus(5, ChronoUnit.MINUTES));

        repository.save(verificationToken);
    }

    public void verifyPasswordResetToken(User user, String resetToken) {
        VerificationToken verificationToken = repository
                .findByUserIdAndTypeAndUsedFalse(user.getId(), VerificationType.PASSWORD_RESET)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired reset token."));

        if (verificationToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Reset token has expired.");
        }

        // Compare the provided token with the stored token
        if (!resetToken.equals(verificationToken.getToken())) {
            throw new BadRequestException("Invalid reset token.");
        }

        // Mark the token as used
        verificationToken.setUsed(true);
        verificationToken.setUsedAt(Instant.now());
        repository.save(verificationToken);
    }
}