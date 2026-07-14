package com.example.eduspace.refresh.repository;

import com.example.eduspace.refresh.entity.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

    void deleteByUserId(String userId);
}