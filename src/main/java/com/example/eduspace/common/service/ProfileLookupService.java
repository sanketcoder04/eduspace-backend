package com.example.eduspace.common.service;

import com.example.eduspace.common.enums.Role;
import com.example.eduspace.student.entity.StudentProfile;
import com.example.eduspace.student.repository.StudentRepository;
import com.example.eduspace.teacher.entity.TeacherProfile;
import com.example.eduspace.teacher.repository.TeacherRepository;
import com.example.eduspace.user.entity.User;
import com.example.eduspace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public record ContactInfo(String email, String phoneNumber) {}

    public Map<String, ProfileSummary> findProfileSummaries(Collection<User> users) {
        if (users.isEmpty()) return Map.of();

        Map<String, User> usersById = users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<String> teacherIds = users.stream().filter(u -> u.getRole() == Role.TEACHER).map(User::getId).toList();
        List<String> studentIds = users.stream().filter(u -> u.getRole() == Role.STUDENT).map(User::getId).toList();

        Map<String, ProfileSummary> summaries = new HashMap<>();

        if (!teacherIds.isEmpty()) {
            teacherRepository.findByUserIdIn(teacherIds).forEach(profile -> summaries.put(
                    profile.getUserId(),
                    new ProfileSummary(usersById.get(profile.getUserId()).getName(), profile.getAvatarUrl())));
        }

        if (!studentIds.isEmpty()) {
            studentRepository.findByUserIdIn(studentIds).forEach(profile -> summaries.put(
                    profile.getUserId(),
                    new ProfileSummary(usersById.get(profile.getUserId()).getName(), profile.getAvatarUrl())));
        }

        return summaries;
    }

    /** Single-user convenience, built on the batch method so the logic lives in one place. */
    public Optional<ProfileSummary> findProfileSummary(User user) {
        return Optional.ofNullable(findProfileSummaries(List.of(user)).get(user.getId()));
    }

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

    public ContactInfo getContactInfo(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return new ContactInfo(null, null);

        String phoneNumber = switch (user.getRole()) {
            case TEACHER -> teacherRepository.findByUserId(userId).map(TeacherProfile::getPhoneNumber).orElse(null);
            case STUDENT -> studentRepository.findByUserId(userId).map(StudentProfile::getPhoneNumber).orElse(null);
        };

        return new ContactInfo(user.getEmail(), phoneNumber);
    }
}