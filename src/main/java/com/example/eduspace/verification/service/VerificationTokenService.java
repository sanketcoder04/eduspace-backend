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
        repository.deleteByUserIdAndType(
                user.getId(),
                VerificationType.EMAIL_VERIFICATION
        );
        String otp = OtpUtil.generateOtp();

        VerificationToken token = VerificationToken.builder()
                .user(user)
                .token(passwordEncoder.encode(otp))
                .type(VerificationType.EMAIL_VERIFICATION)
                .expiresAt(Instant.now().plus(10, ChronoUnit.MINUTES))
                .used(false)
                .build();

        repository.save(token);
        return otp;
    }

    public VerificationToken getEmailVerificationToken(User user) {
        return repository.findByUserIdAndTypeAndUsedFalse(user.getId(), VerificationType.EMAIL_VERIFICATION)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Verification token not found.")
                );

    }

    public void verifyEmailOtp(User user, String otp) {
        VerificationToken token = repository.findByUserIdAndTypeAndUsedFalse(
                user.getId(), VerificationType.EMAIL_VERIFICATION
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Verification token not found.")
                );

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("OTP has expired.");
        }

        if (!passwordEncoder.matches(otp, token.getToken())) {
            throw new BadRequestException("Invalid OTP.");
        }
        token.setUsed(true);
        token.setUsedAt(Instant.now());

        repository.save(token);
    }

    public void verifyOtp(User user, String otp) {
        VerificationToken token = getEmailVerificationToken(user);

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("OTP has expired.");
        }

        if (!passwordEncoder.matches(otp, token.getToken())) {
            throw new BadRequestException("Invalid OTP.");
        }

        token.setUsed(true);
        token.setUsedAt(Instant.now());

        repository.save(token);
    }
}