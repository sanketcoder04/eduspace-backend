package com.example.eduspace.refresh.service;

import com.example.eduspace.refresh.entity.RefreshToken;
import com.example.eduspace.refresh.repository.RefreshTokenRepository;
import com.example.eduspace.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public void saveRefreshToken(User user, String token, Instant expiresAt) {
        repository.deleteByUserId(user.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .token(token)
                .expiresAt(expiresAt)
                .revoked(false)
                .build();

        repository.save(refreshToken);
    }

    public RefreshToken getRefreshToken(String token) {
        return repository.findByTokenAndRevokedFalse(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found."));
    }

    public void revoke(String token) {
        RefreshToken refreshToken = getRefreshToken(token);

        refreshToken.setRevoked(true);
        refreshToken.setRevokedAt(Instant.now());

        repository.save(refreshToken);
    }

    @Transactional
    public void revokeAll(User user) {
        repository.deleteByUserId(user.getId());
    }
}