package com.example.eduspace.student.service;

import com.example.eduspace.common.dto.AddCertificateRequest;
import com.example.eduspace.common.dto.EducationDto;
import com.example.eduspace.common.dto.SubmitVerificationRequest;
import com.example.eduspace.common.dto.UpdateCertificateRequest;
import com.example.eduspace.common.entity.Certificate;
import com.example.eduspace.common.entity.ProfileVerification;
import com.example.eduspace.common.enums.VerificationStatus;
import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.student.dto.request.UpdateStudentBasicInfoRequest;
import com.example.eduspace.student.dto.response.StudentProfileResponse;
import com.example.eduspace.student.entity.StudentProfile;
import com.example.eduspace.student.mapper.StudentProfileMapper;
import com.example.eduspace.student.repository.StudentRepository;
import com.example.eduspace.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentRepository studentRepository;

    private final StudentProfileMapper mapper;

    public StudentProfileResponse getMyProfile(User user) {
        return mapper.toResponse(getOrCreateProfile(user), user);
    }

    public StudentProfileResponse updateBasicInfo(User user, UpdateStudentBasicInfoRequest request) {
        StudentProfile profile = getOrCreateProfile(user);

        profile.setName(request.getName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setAddress(mapper.toAddress(request.getAddress()));
        profile.setParentName(request.getParentName());
        profile.setParentPhoneNumber(request.getParentPhoneNumber());
        profile.setParentEmail(request.getParentEmail());
        profile.setGender(request.getGender());
        profile.setHeadline(request.getHeadline());
        profile.setAbout(request.getAbout());

        return save(profile, user);
    }

    public StudentProfileResponse updateEducation(User user, List<EducationDto> educationDtos) {
        StudentProfile profile = getOrCreateProfile(user);

        List<com.example.eduspace.common.entity.Education> education = mapper.toEducationList(educationDtos);
        education.forEach(entry -> {
            if (entry.getId() == null || entry.getId().isBlank()) {
                entry.setId(UUID.randomUUID().toString());
            }
        });

        profile.setEducation(education);

        return save(profile, user);
    }

    public StudentProfileResponse submitVerification(User user, SubmitVerificationRequest request) {
        StudentProfile profile = getOrCreateProfile(user);

        // MVP: auto-approve on submission — same note as TeacherProfileService;
        // swap for a real face-match/liveness provider later.
        ProfileVerification verification = ProfileVerification.builder()
                .status(VerificationStatus.VERIFIED)
                .selfieUrl(request.getSelfieUrl())
                .verifiedAddress(mapper.toAddress(request.getAddress()))
                .locationVerified(true)
                .faceVerified(true)
                .submittedAt(Instant.now())
                .verifiedAt(Instant.now())
                .build();

        profile.setVerification(verification);

        return save(profile, user);
    }

    public StudentProfileResponse updateAvatar(User user, String avatarUrl) {
        StudentProfile profile = getOrCreateProfile(user);
        profile.setAvatarUrl(avatarUrl);
        return save(profile, user);
    }

    public StudentProfileResponse updateCover(User user, String coverImageUrl) {
        StudentProfile profile = getOrCreateProfile(user);
        profile.setCoverImageUrl(coverImageUrl);
        return save(profile, user);
    }

    public StudentProfileResponse addCertificate(User user, AddCertificateRequest request) {
        StudentProfile profile = getOrCreateProfile(user);

        Certificate certificate = Certificate.builder()
                .id(UUID.randomUUID().toString())
                .title(request.getTitle())
                .url(request.getUrl())
                .uploadedAt(Instant.now())
                .build();

        List<Certificate> certificates = new ArrayList<>(profile.getCertificates());
        certificates.add(certificate);
        profile.setCertificates(certificates);

        return save(profile, user);
    }

    public StudentProfileResponse updateCertificate(User user, String certificateId, UpdateCertificateRequest request) {
        StudentProfile profile = getOrCreateProfile(user);

        List<Certificate> certificates = new ArrayList<>(profile.getCertificates());
        Certificate existing = certificates.stream()
                .filter(c -> c.getId().equals(certificateId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found."));

        existing.setTitle(request.getTitle());
        existing.setUrl(request.getUrl());

        profile.setCertificates(certificates);

        return save(profile, user);
    }

    public StudentProfileResponse deleteCertificate(User user, String certificateId) {
        StudentProfile profile = getOrCreateProfile(user);

        List<Certificate> certificates = new ArrayList<>(profile.getCertificates());
        boolean removed = certificates.removeIf(c -> c.getId().equals(certificateId));

        if (!removed) {
            throw new ResourceNotFoundException("Certificate not found.");
        }

        profile.setCertificates(certificates);

        return save(profile, user);
    }

    private StudentProfile getOrCreateProfile(User user) {
        return studentRepository.findByUserId(user.getId())
                .orElseGet(() -> studentRepository.save(
                        StudentProfile.builder()
                                .userId(user.getId())
                                .name(user.getName())
                                .profileCompleted(false)
                                .profileCompletionPercent(0)
                                .build()
                ));
    }

    private StudentProfileResponse save(StudentProfile profile, User user) {
        recalcCompletion(profile);
        StudentProfile saved = studentRepository.save(profile);
        return mapper.toResponse(saved, user);
    }

    private void recalcCompletion(StudentProfile profile) {
        int percent = 0;

        boolean hasBasicInfo = profile.getName() != null && profile.getPhoneNumber() != null
                && profile.getAddress() != null && profile.getParentName() != null;
        boolean hasEducation = !profile.getEducation().isEmpty();
        boolean isVerified = profile.getVerification() != null
                && profile.getVerification().getStatus() != VerificationStatus.NOT_SUBMITTED;

        if (hasBasicInfo) percent += 34;
        if (hasEducation) percent += 33;
        if (isVerified) percent += 33;

        profile.setProfileCompletionPercent(percent);
        profile.setProfileCompleted(percent == 100);
    }
}