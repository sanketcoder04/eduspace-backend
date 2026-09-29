package com.example.eduspace.user.repository;

import com.example.eduspace.user.entity.User;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findTop8ByNameContainingIgnoreCaseAndIdNot(String namePart, String excludeId);
}