package com.example.eduspace.application.service;

import com.example.eduspace.application.dto.request.ContactShareConsentRequest;
import com.example.eduspace.application.dto.response.ApplicationResponse;
import com.example.eduspace.application.entity.Application;
import com.example.eduspace.application.entity.ContactShareConsent;
import com.example.eduspace.application.enums.ApplicationStatus;
import com.example.eduspace.application.mapper.ApplicationMapper;
import com.example.eduspace.application.repository.ApplicationRepository;
import com.example.eduspace.chat.service.ChatService;
import com.example.eduspace.common.enums.Role;
import com.example.eduspace.common.service.ProfileLookupService;
import com.example.eduspace.exception.BadRequestException;
import com.example.eduspace.exception.ForbiddenException;
import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.notification.enums.NotificationType;
import com.example.eduspace.notification.service.NotificationService;
import com.example.eduspace.opportunity.entity.Opportunity;
import com.example.eduspace.opportunity.enums.ClassFormat;
import com.example.eduspace.opportunity.enums.OpportunityStatus;
import com.example.eduspace.opportunity.enums.PostType;
import com.example.eduspace.opportunity.service.OpportunityService;
import com.example.eduspace.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    private final OpportunityService opportunityService;

    private final ChatService chatService;

    private final NotificationService notificationService;

    private final ApplicationMapper mapper;

    private final ProfileLookupService profileLookupService;

    private static final List<ApplicationStatus> ACTIVE_STATUSES =
            List.of(ApplicationStatus.PENDING, ApplicationStatus.APPROVED);

    public ApplicationResponse apply(User applicant, String opportunityId, String message) {
        Opportunity opportunity = opportunityService.getEntity(opportunityId);

        if (opportunity.getStatus() != OpportunityStatus.OPEN
                && opportunity.getStatus() != OpportunityStatus.PARTIALLY_FILLED) {
            throw new BadRequestException("This posting is no longer accepting applications.");
        }

        if (opportunity.getAuthorId().equals(applicant.getId())) {
            throw new BadRequestException("You cannot apply to your own posting.");
        }

        Role expectedApplicantRole = opportunity.getPostType() == PostType.TEACHING_OPENING
                ? Role.STUDENT
                : Role.TEACHER;

        if (applicant.getRole() != expectedApplicantRole) {
            throw new ForbiddenException("This posting isn't open to your account type.");
        }

        applicationRepository
                .findByOpportunityIdAndApplicantIdAndStatusIn(opportunityId, applicant.getId(), ACTIVE_STATUSES)
                .ifPresent(existing -> {
                    throw new BadRequestException("You already have an active application for this posting.");
                });

        Application application = Application.builder()
                .opportunityId(opportunityId)
                .applicantId(applicant.getId())
                .authorId(opportunity.getAuthorId())
                .message(message)
                .status(ApplicationStatus.PENDING)
                .contactShareConsent(ContactShareConsent.builder().build())
                .build();

        Application saved = applicationRepository.save(application);

        opportunity.setApplicationsCount(opportunity.getApplicationsCount() + 1);
        opportunityService.save(opportunity);

        notificationService.notify(
                opportunity.getAuthorId(),
                NotificationType.APPLICATION_RECEIVED,
                "New application received",
                applicant.getName() + " applied to \"" + opportunity.getTitle() + "\".",
                "APPLICATION",
                saved.getId()
        );

        return enrich(saved, opportunity.getTitle());
    }

    @Transactional
    public ApplicationResponse approve(User author, String applicationId) {
        Application application = getOwnedByAuthor(author, applicationId);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Only a pending application can be approved.");
        }

        application.setStatus(ApplicationStatus.APPROVED);
        application.setRespondedAt(Instant.now());
        Application saved = applicationRepository.save(application);

        Opportunity opportunity = opportunityService.getEntity(application.getOpportunityId());
        fillSeat(opportunity);

        chatService.startConversation(saved);

        notificationService.notify(
                application.getApplicantId(),
                NotificationType.APPLICATION_APPROVED,
                "Application approved",
                "Your application for \"" + opportunity.getTitle() + "\" was approved. You can now chat.",
                "APPLICATION",
                saved.getId()
        );

        return enrich(saved, opportunity.getTitle());
    }

    @Transactional
    public ApplicationResponse reject(User author, String applicationId, String reason) {
        Application application = getOwnedByAuthor(author, applicationId);

        if (application.getStatus() == ApplicationStatus.WITHDRAWN
                || application.getStatus() == ApplicationStatus.REJECTED) {
            throw new BadRequestException("This application has already been closed.");
        }

        boolean wasApproved = application.getStatus() == ApplicationStatus.APPROVED;

        application.setStatus(ApplicationStatus.REJECTED);
        application.setDecisionReason(reason);
        application.setRespondedAt(Instant.now());
        Application saved = applicationRepository.save(application);

        Opportunity opportunity = opportunityService.getEntity(application.getOpportunityId());

        if (wasApproved) {
            freeSeatAndCloseChat(saved, opportunity);
        }

        notificationService.notify(
                application.getApplicantId(),
                NotificationType.APPLICATION_REJECTED,
                "Application rejected",
                "Your application was rejected by the author.",
                "APPLICATION",
                saved.getId()
        );

        return enrich(saved, opportunity.getTitle());
    }

    @Transactional
    public ApplicationResponse withdraw(User applicant, String applicationId) {
        Application application = applicationRepository.findByIdAndApplicantId(applicationId, applicant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found."));

        if (application.getStatus() == ApplicationStatus.WITHDRAWN
                || application.getStatus() == ApplicationStatus.REJECTED) {
            throw new BadRequestException("This application has already been closed.");
        }

        boolean wasApproved = application.getStatus() == ApplicationStatus.APPROVED;

        application.setStatus(ApplicationStatus.WITHDRAWN);
        application.setRespondedAt(Instant.now());
        Application saved = applicationRepository.save(application);

        Opportunity opportunity = opportunityService.getEntity(application.getOpportunityId());

        if (wasApproved) {
            freeSeatAndCloseChat(saved, opportunity);
        }

        notificationService.notify(
                application.getAuthorId(),
                NotificationType.APPLICATION_WITHDRAWN,
                "Application withdrawn",
                "An applicant withdrew their application.",
                "APPLICATION",
                saved.getId()
        );

        return enrich(saved, opportunity.getTitle());
    }

    public ApplicationResponse updateContactConsent(User author, String applicationId, ContactShareConsentRequest request) {
        Application application = getOwnedByAuthor(author, applicationId);

        if (application.getStatus() != ApplicationStatus.APPROVED) {
            throw new BadRequestException("Contact details can only be shared for an approved application.");
        }

        application.setContactShareConsent(ContactShareConsent.builder()
                .phoneShared(request.isPhoneShared())
                .emailShared(request.isEmailShared())
                .updatedAt(Instant.now())
                .build());

        Application saved = applicationRepository.save(application);

        chatService.postContactShareUpdate(saved);

        notificationService.notify(
                application.getApplicantId(),
                NotificationType.CONTACT_SHARED,
                "Contact details updated",
                "The author updated what contact details are shared with you.",
                "APPLICATION",
                saved.getId()
        );

        Opportunity opportunity = opportunityService.getEntity(application.getOpportunityId());
        return enrich(saved, opportunity.getTitle());
    }

    public Page<ApplicationResponse> getSentApplications(User applicant, Pageable pageable) {
        return applicationRepository.findByApplicantId(applicant.getId(), pageable)
                .map(app -> enrich(app, resolveTitleSafely(app)));
    }

    public Page<ApplicationResponse> getReceivedApplications(User author, Pageable pageable) {
        return applicationRepository.findByAuthorId(author.getId(), pageable)
                .map(app -> enrich(app, resolveTitleSafely(app)));
    }

    /** Internal accessor for ChatService, which needs the raw entity to validate participants. */
    public Application getEntity(String id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found."));
    }

    private void fillSeat(Opportunity opportunity) {
        if (opportunity.getClassFormat() == ClassFormat.PERSONALIZED) {
            opportunity.setStatus(OpportunityStatus.CLOSED);
            opportunityService.save(opportunity);
        } else {
            opportunityService.adjustSeatState(opportunity, +1);
        }
    }

    private void freeSeatAndCloseChat(Application application, Opportunity opportunity) {
        if (opportunity.getClassFormat() == ClassFormat.PERSONALIZED) {
            opportunity.setStatus(OpportunityStatus.OPEN);
            opportunityService.save(opportunity);
        } else {
            opportunityService.adjustSeatState(opportunity, -1);
        }

        chatService.closeConversation(application.getId());
    }

    private Application getOwnedByAuthor(User author, String applicationId) {
        return applicationRepository.findByIdAndAuthorId(applicationId, author.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found."));
    }

    private String resolveTitleSafely(Application application) {
        // Used for list views where a missing/deleted opportunity shouldn't 500 the whole page.
        try {
            return opportunityService.getEntity(application.getOpportunityId()).getTitle();
        } catch (ResourceNotFoundException ex) {
            return "Deleted posting";
        }
    }

    private ApplicationResponse enrich(Application application, String opportunityTitle) {
        ApplicationResponse response = mapper.toResponse(application);
        response.setOpportunityTitle(opportunityTitle);

        ProfileLookupService.ProfileSummary applicant = profileLookupService.getSummary(application.getApplicantId());
        response.setApplicantName(applicant.name());
        response.setApplicantAvatarUrl(applicant.avatarUrl());

        return response;
    }
}