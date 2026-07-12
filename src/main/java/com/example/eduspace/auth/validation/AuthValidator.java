package com.example.eduspace.auth.validation;

import com.example.eduspace.exception.BadRequestException;
import com.example.eduspace.exception.ConflictException;
import com.example.eduspace.user.entity.User;
import com.example.eduspace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final UserRepository userRepository;

    public void validateRegistration(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("An account with this email already exists.");
        }
    }

    public void validateEmailVerification(User user) {
        if (user.isEmailVerified()) {
            throw new BadRequestException("Email is already verified.");
        }
    }

    public void validateResendOtp(User user) {
        if (user.isEmailVerified()) {
            throw new BadRequestException("Email is already verified.");
        }
    }
}