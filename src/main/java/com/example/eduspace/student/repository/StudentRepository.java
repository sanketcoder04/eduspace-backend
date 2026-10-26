package com.example.eduspace.student.repository;

import com.example.eduspace.student.entity.StudentProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends MongoRepository<StudentProfile, String> {

    Optional<StudentProfile> findByUserId(String userId);

    List<StudentProfile> findByUserIdIn(Collection<String> userIds);
}