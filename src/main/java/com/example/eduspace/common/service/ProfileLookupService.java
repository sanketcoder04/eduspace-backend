package com.example.eduspace.common.service;

import com.example.eduspace.common.enums.Role;
import com.example.eduspace.student.repository.StudentRepository;
import com.example.eduspace.teacher.repository.TeacherRepository;
import com.example.eduspace.user.entity.User;
import com.example.eduspace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Centralizes the "look up a user's display name / avatar for enrichment"
 * logic used by Opportunity, Application, and Chat responses. Avatar lives on
 * TeacherProfile or StudentProfile depending on role, so every service that
 * enriches a DTO with "authorName"/"applicantAvatarUrl" etc. needs this same
 * branch — kept in one place instead of duplicated four times.
 */

@Service
@RequiredArgsConstructor
public class ProfileLookupService {

    private final UserRepository userRepository;

    private final TeacherRepository teacherRepository;

    private final StudentRepository studentRepository;

    public record ProfileSummary(String name, String avatarUrl) {}

    public ProfileSummary getSummary(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return new ProfileSummary("Unknown user", null);
        }

        User user = userOpt.get();

        if (user.getRole() == Role.TEACHER) {
            return teacherRepository.findByUserId(userId)
                    .map(p -> new ProfileSummary(user.getName(), p.getAvatarUrl()))
                    .orElse(new ProfileSummary(user.getName(), null));
        }

        if (user.getRole() == Role.STUDENT) {
            return studentRepository.findByUserId(userId)
                    .map(p -> new ProfileSummary(user.getName(), p.getAvatarUrl()))
                    .orElse(new ProfileSummary(user.getName(), null));
        }

        return new ProfileSummary(user.getName(), null);
    }
}