package com.example.eduspace.teacher.repository;

import com.example.eduspace.teacher.entity.TeacherProfile;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TeacherRepository extends MongoRepository<TeacherProfile, String> {
    Optional<TeacherProfile> findByUserId(String userId);
}