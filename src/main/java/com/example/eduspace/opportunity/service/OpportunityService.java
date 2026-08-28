package com.example.eduspace.opportunity.service;

import com.example.eduspace.common.enums.Role;
import com.example.eduspace.common.enums.VerificationStatus;
import com.example.eduspace.common.service.ProfileLookupService;
import com.example.eduspace.exception.BadRequestException;
import com.example.eduspace.exception.ForbiddenException;
import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.opportunity.dto.request.*;
import com.example.eduspace.opportunity.dto.response.OpportunityResponse;
import com.example.eduspace.opportunity.entity.*;
import com.example.eduspace.opportunity.enums.Mode;
import com.example.eduspace.opportunity.enums.OpportunityStatus;
import com.example.eduspace.opportunity.enums.PostType;
import com.example.eduspace.opportunity.mapper.OpportunityMapper;
import com.example.eduspace.opportunity.repository.OpportunityRepository;
import com.example.eduspace.teacher.entity.TeacherProfile;
import com.example.eduspace.teacher.repository.TeacherRepository;
import com.example.eduspace.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;

    private final TeacherRepository teacherRepository;

    private final OpportunityMapper mapper;

    private final ProfileLookupService profileLookupService;

    public OpportunityResponse createTeachingOpening(User author, CreateTeachingOpeningRequest request) {
        if (author.getRole() != Role.TEACHER) {
            throw new ForbiddenException("Only teachers can post a teaching opening.");
        }

        TeacherProfile teacherProfile = teacherRepository.findByUserId(author.getId())
                .orElseThrow(() -> new ForbiddenException("Complete your teacher profile before posting."));

        boolean isVerified = teacherProfile.getVerification() != null
                && teacherProfile.getVerification().getStatus() == VerificationStatus.VERIFIED;

        if (!isVerified) {
            throw new ForbiddenException("Complete profile verification before posting a teaching opening.");
        }

        validateSubjectsAgainstOfferings(teacherProfile, request.getSubjects());
        validateLocation(request.getMode(), request.getLocation());

        Opportunity opportunity = mapper.toOpportunity(request, author);
        opportunity.setPostType(PostType.TEACHING_OPENING);
        opportunity.setStatus(OpportunityStatus.OPEN);
        opportunity.setApplicationsCount(0);
        opportunity.setTeachingOpeningDetails(mapper.toTeachingOpeningDetails(request));

        Opportunity saved = opportunityRepository.save(opportunity);
        return enrich(saved);
    }

    public OpportunityResponse createTuitionRequirement(User author, CreateTuitionRequirementRequest request) {
        if (author.getRole() != Role.STUDENT) {
            throw new ForbiddenException("Only students can post a tuition requirement.");
        }

        validateLocation(request.getMode(), request.getLocation());

        Opportunity opportunity = mapper.toOpportunity(request, author);
        opportunity.setPostType(PostType.TUITION_REQUIREMENT);
        opportunity.setStatus(OpportunityStatus.OPEN);
        opportunity.setApplicationsCount(0);
        opportunity.setTuitionRequirementDetails(mapper.toTuitionRequirementDetails(request));

        Opportunity saved = opportunityRepository.save(opportunity);
        return enrich(saved);
    }

    public Page<OpportunityResponse> search(OpportunityFilterRequest filter, Pageable pageable) {
        return opportunityRepository.search(filter, pageable).map(this::enrich);
    }

    public OpportunityResponse getById(String id) {
        return enrich(getEntity(id));
    }

    public OpportunityResponse close(User author, String opportunityId) {
        Opportunity opportunity = getOwned(author, opportunityId);
        opportunity.setStatus(OpportunityStatus.CLOSED);
        return enrich(opportunityRepository.save(opportunity));
    }

    public OpportunityResponse reopen(User author, String opportunityId) {
        Opportunity opportunity = getOwned(author, opportunityId);
        opportunity.setStatus(OpportunityStatus.OPEN);
        return enrich(opportunityRepository.save(opportunity));
    }

    /**
     * Used internally by ApplicationService when an approval fills a seat, or
     * a withdrawal/rejection frees one. Returns the entity, not a response —
     * this is a service-to-service call, never a controller boundary.
     */
    public Opportunity adjustSeatState(Opportunity opportunity, int seatDelta) {
        if (opportunity.getTeachingOpeningDetails() == null
                || opportunity.getTeachingOpeningDetails().getBatchCapacity() == null) {
            return opportunity;
        }

        TeachingOpeningDetails details = opportunity.getTeachingOpeningDetails();
        int newSeatsFilled = Math.max(0, details.getSeatsFilled() + seatDelta);
        details.setSeatsFilled(newSeatsFilled);

        if (newSeatsFilled >= details.getBatchCapacity()) {
            opportunity.setStatus(OpportunityStatus.CLOSED);
        } else if (newSeatsFilled > 0) {
            opportunity.setStatus(OpportunityStatus.PARTIALLY_FILLED);
        } else {
            opportunity.setStatus(OpportunityStatus.OPEN);
        }

        return opportunityRepository.save(opportunity);
    }

    /** Internal accessor for other services (ApplicationService) that need the raw entity. */
    public Opportunity getEntity(String id) {
        return opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found."));
    }

    public void save(Opportunity opportunity) {
        opportunityRepository.save(opportunity);
    }

    private Opportunity getOwned(User author, String opportunityId) {
        Opportunity opportunity = getEntity(opportunityId);
        if (!opportunity.getAuthorId().equals(author.getId())) {
            throw new ForbiddenException("You do not own this opportunity.");
        }
        return opportunity;
    }

    private void validateLocation(Mode mode, Object location) {
        if (mode != Mode.ONLINE && location == null) {
            throw new BadRequestException("Location is required for offline or hybrid postings.");
        }
    }

    private OpportunityResponse enrich(Opportunity opportunity) {
        OpportunityResponse response = mapper.toResponse(opportunity);

        ProfileLookupService.ProfileSummary author = profileLookupService.getSummary(opportunity.getAuthorId());
        response.setAuthorName(author.name());
        response.setAuthorAvatarUrl(author.avatarUrl());

        return response;
    }

    /**
     * A teacher may only advertise subjects already declared in their profile's
     * subjectOfferings — this keeps postings trustworthy (no claiming to teach
     * something never verified/reviewed on the profile) and pushes teachers to
     * keep their subject list current rather than let postings drift ahead of it.
     */
    private void validateSubjectsAgainstOfferings(TeacherProfile teacherProfile, List<String> requestedSubjects) {
        Set<String> offeredSubjects = teacherProfile.getSubjectOfferings().stream()
                .map(offering -> offering.getSubjectName().trim().toLowerCase())
                .collect(Collectors.toSet());

        List<String> notOffered = requestedSubjects.stream()
                .filter(subject -> !offeredSubjects.contains(subject.trim().toLowerCase()))
                .toList();

        if (!notOffered.isEmpty()) {
            throw new BadRequestException(
                    "These subjects aren't in your profile's subject offerings yet: "
                            + String.join(", ", notOffered)
                            + ". Add them to your profile before posting."
            );
        }
    }
}