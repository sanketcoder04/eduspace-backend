package com.example.eduspace.teacher.service;

import com.example.eduspace.common.dto.SubmitVerificationRequest;
import com.example.eduspace.common.entity.Certificate;
import com.example.eduspace.common.entity.ProfileVerification;
import com.example.eduspace.common.enums.VerificationStatus;
import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.teacher.dto.request.AddSubjectOfferingRequest;
import com.example.eduspace.teacher.dto.request.UpdateSubjectOfferingRequest;
import com.example.eduspace.teacher.dto.request.UpdateTeacherBasicInfoRequest;
import com.example.eduspace.common.dto.EducationDto;
import com.example.eduspace.teacher.dto.response.TeacherProfileResponse;
import com.example.eduspace.teacher.entity.SubjectOffering;
import com.example.eduspace.teacher.entity.TeacherProfile;
import com.example.eduspace.teacher.mapper.TeacherProfileMapper;
import com.example.eduspace.teacher.repository.TeacherRepository;
import com.example.eduspace.common.dto.AddCertificateRequest;
import com.example.eduspace.common.dto.UpdateCertificateRequest;
import com.example.eduspace.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherProfileService {

    private final TeacherRepository teacherRepository;

    private final TeacherProfileMapper mapper;

    public TeacherProfileResponse getMyProfile(User user) {
        return mapper.toResponse(getOrCreateProfile(user), user);
    }

    public TeacherProfileResponse updateBasicInfo(User user, UpdateTeacherBasicInfoRequest request) {
        TeacherProfile profile = getOrCreateProfile(user);

        profile.setName(request.getName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setAddress(mapper.toAddress(request.getAddress()));
        profile.setGender(request.getGender());
        profile.setHeadline(request.getHeadline());
        profile.setAbout(request.getAbout());

        return save(profile, user);
    }

    public TeacherProfileResponse updateEducation(User user, List<EducationDto> educationDtos) {
        TeacherProfile profile = getOrCreateProfile(user);

        List<com.example.eduspace.common.entity.Education> education = mapper.toEducationList(educationDtos);
        education.forEach(entry -> {
            if (entry.getId() == null || entry.getId().isBlank()) {
                entry.setId(UUID.randomUUID().toString());
            }
        });

        profile.setEducation(education);

        return save(profile, user);
    }

    public TeacherProfileResponse addSubjectOffering(User user, AddSubjectOfferingRequest request) {
        TeacherProfile profile = getOrCreateProfile(user);

        SubjectOffering offering = SubjectOffering.builder()
                .id(UUID.randomUUID().toString())
                .subjectName(request.getSubjectName())
                .qualificationLevel(request.getQualificationLevel())
                .addedAt(Instant.now())
                .build();

        List<SubjectOffering> offerings = new ArrayList<>(profile.getSubjectOfferings());
        offerings.add(offering);
        profile.setSubjectOfferings(offerings);

        return save(profile, user);
    }

    public TeacherProfileResponse updateSubjectOffering(User user, String subjectId, UpdateSubjectOfferingRequest request) {

        TeacherProfile profile = getOrCreateProfile(user);

        List<SubjectOffering> offerings = new ArrayList<>(profile.getSubjectOfferings());
        SubjectOffering existing = offerings.stream()
                .filter(o -> o.getId().equals(subjectId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Subject offering not found."));

        existing.setSubjectName(request.getSubjectName());
        existing.setQualificationLevel(request.getQualificationLevel());
        existing.setUpdatedAt(Instant.now());

        profile.setSubjectOfferings(offerings);

        return save(profile, user);
    }

    public TeacherProfileResponse deleteSubjectOffering(User user, String subjectId) {
        TeacherProfile profile = getOrCreateProfile(user);

        List<SubjectOffering> offerings = new ArrayList<>(profile.getSubjectOfferings());
        boolean removed = offerings.removeIf(o -> o.getId().equals(subjectId));

        if (!removed) {
            throw new ResourceNotFoundException("Subject offering not found.");
        }

        profile.setSubjectOfferings(offerings);

        return save(profile, user);
    }

    public TeacherProfileResponse updateAvatar(User user, String avatarUrl) {
        TeacherProfile profile = getOrCreateProfile(user);
        profile.setAvatarUrl(avatarUrl);
        return save(profile, user);
    }

    public TeacherProfileResponse updateCover(User user, String coverImageUrl) {
        TeacherProfile profile = getOrCreateProfile(user);
        profile.setCoverImageUrl(coverImageUrl);
        return save(profile, user);
    }

    public TeacherProfileResponse updateResume(User user, String resumeUrl) {
        TeacherProfile profile = getOrCreateProfile(user);
        profile.setResumeUrl(resumeUrl);
        return save(profile, user);
    }

    public TeacherProfileResponse deleteResume(User user) {
        TeacherProfile profile = getOrCreateProfile(user);
        profile.setResumeUrl(null);
        return save(profile, user);
    }

    public TeacherProfileResponse addCertificate(User user, AddCertificateRequest request) {
        TeacherProfile profile = getOrCreateProfile(user);

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

    public TeacherProfileResponse updateCertificate(User user, String certificateId, UpdateCertificateRequest request) {
        TeacherProfile profile = getOrCreateProfile(user);

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

    public TeacherProfileResponse deleteCertificate(User user, String certificateId) {
        TeacherProfile profile = getOrCreateProfile(user);

        List<Certificate> certificates = new ArrayList<>(profile.getCertificates());
        boolean removed = certificates.removeIf(c -> c.getId().equals(certificateId));

        if (!removed) {
            throw new ResourceNotFoundException("Certificate not found.");
        }
        profile.setCertificates(certificates);
        return save(profile, user);
    }

    public TeacherProfileResponse submitVerification(User user, SubmitVerificationRequest request) {
        TeacherProfile profile = getOrCreateProfile(user);

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

    private TeacherProfile getOrCreateProfile(User user) {
        return teacherRepository.findByUserId(user.getId())
                .orElseGet(() -> teacherRepository.save(
                        TeacherProfile.builder()
                                .userId(user.getId())
                                .name(user.getName())
                                .profileCompleted(false)
                                .profileCompletionPercent(0)
                                .build()
                ));
    }

    private TeacherProfileResponse save(TeacherProfile profile, User user) {
        recalcCompletion(profile);
        TeacherProfile saved = teacherRepository.save(profile);
        return mapper.toResponse(saved, user);
    }

    private void recalcCompletion(TeacherProfile profile) {
        int percent = 0;

        boolean hasBasicInfo = profile.getName() != null && profile.getPhoneNumber() != null
                && profile.getAddress() != null;
        boolean hasEducation = !profile.getEducation().isEmpty();
        boolean hasSubjectOffering = !profile.getSubjectOfferings().isEmpty();
        boolean isVerified = profile.getVerification() != null
                && profile.getVerification().getStatus() != VerificationStatus.NOT_SUBMITTED;

        if (hasBasicInfo) percent += 25;
        if (hasEducation) percent += 25;
        if (hasSubjectOffering) percent += 25;
        if (isVerified) percent += 25;

        profile.setProfileCompletionPercent(percent);
        profile.setProfileCompleted(percent == 100);
    }
}