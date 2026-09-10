package com.example.eduspace.user.service;

import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.user.dto.response.UserSummaryResponse;
import com.example.eduspace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Deliberately minimal — just enough for the frontend to learn WHICH
     * role-specific "view" endpoint to call next (teacher vs student), not a
     * general-purpose user lookup. Never exposes email/phone here.
     */
    public UserSummaryResponse getSummary(String userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        return UserSummaryResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .role(user.getRole())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}