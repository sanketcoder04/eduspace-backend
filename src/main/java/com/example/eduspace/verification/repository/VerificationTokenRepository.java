package com.example.eduspace.verification.repository;

import com.example.eduspace.verification.entity.VerificationToken;
import com.example.eduspace.verification.enums.VerificationType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {

    Optional<VerificationToken> findByUserIdAndTypeAndUsedFalse(String userId, VerificationType type);

    void deleteByUserIdAndType(String userId, VerificationType type);
}