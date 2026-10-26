package com.example.eduspace.user.repository;

import com.example.eduspace.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Slice<User> findByNameContainingIgnoreCaseAndIdNot(String namePart, String excludeId, Pageable pageable);

    List<User> findByIdNotIn(List<String> excludedIds, Pageable pageable);
}