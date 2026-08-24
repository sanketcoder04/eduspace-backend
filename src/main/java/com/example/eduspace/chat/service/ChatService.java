package com.example.eduspace.chat.service;

import com.example.eduspace.application.entity.Application;
import com.example.eduspace.chat.dto.response.ConversationResponse;
import com.example.eduspace.chat.dto.response.MessageResponse;
import com.example.eduspace.chat.entity.Conversation;
import com.example.eduspace.chat.entity.Message;
import com.example.eduspace.chat.enums.ConversationStatus;
import com.example.eduspace.chat.enums.MessageType;
import com.example.eduspace.chat.mapper.ChatMapper;
import com.example.eduspace.chat.repository.ConversationRepository;
import com.example.eduspace.chat.repository.MessageRepository;
import com.example.eduspace.common.service.ProfileLookupService;
import com.example.eduspace.exception.BadRequestException;
import com.example.eduspace.exception.ForbiddenException;
import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.notification.enums.NotificationType;
import com.example.eduspace.notification.service.NotificationService;
import com.example.eduspace.opportunity.entity.Opportunity;
import com.example.eduspace.opportunity.service.OpportunityService;
import com.example.eduspace.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepository;

    private final MessageRepository messageRepository;

    private final NotificationService notificationService;

    private final OpportunityService opportunityService;

    private final ChatMapper mapper;
    private final ProfileLookupService profileLookupService;

    /** Called by ApplicationService with a freshly-saved Application entity — no response needed here. */
    public void startConversation(Application application) {
        Conversation conversation = Conversation.builder()
                .applicationId(application.getId())
                .opportunityId(application.getOpportunityId())
                .authorId(application.getAuthorId())
                .applicantId(application.getApplicantId())
                .status(ConversationStatus.ACTIVE)
                .build();

        Conversation saved = conversationRepository.save(conversation);

        postSystemMessage(saved, "You're both connected — say hello!");
    }

    public void closeConversation(String applicationId) {
        conversationRepository.findByApplicationId(applicationId).ifPresent(conversation -> {
            conversation.setStatus(ConversationStatus.CLOSED);
            conversationRepository.save(conversation);
            postSystemMessage(conversation, "This application was closed. The chat is now read-only.");
        });
    }

    public void postContactShareUpdate(com.example.eduspace.application.entity.Application application) {
        conversationRepository.findByApplicationId(application.getId()).ifPresent(conversation -> {
            String summary = describeConsent(application);
            Message message = Message.builder()
                    .conversationId(conversation.getId())
                    .senderId(null)
                    .type(MessageType.CONTACT_SHARE_UPDATE)
                    .content(summary)
                    .build();
            messageRepository.save(message);
            touchConversation(conversation, summary);
        });
    }

    public MessageResponse sendMessage(User sender, String conversationId, String content) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found."));

        assertParticipant(conversation, sender.getId());

        if (conversation.getStatus() != ConversationStatus.ACTIVE) {
            throw new BadRequestException("This conversation is closed.");
        }

        Message message = Message.builder()
                .conversationId(conversationId)
                .senderId(sender.getId())
                .type(MessageType.TEXT)
                .content(content)
                .build();

        Message saved = messageRepository.save(message);
        touchConversation(conversation, content);

        String recipientId = conversation.getAuthorId().equals(sender.getId())
                ? conversation.getApplicantId()
                : conversation.getAuthorId();

        notificationService.notify(
                recipientId,
                NotificationType.NEW_MESSAGE,
                "New message",
                content.length() > 80 ? content.substring(0, 80) + "…" : content,
                "CONVERSATION",
                conversationId
        );

        return enrichMessage(saved, sender.getName());
    }

    public Page<MessageResponse> getMessages(User requester, String conversationId, Pageable pageable) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found."));

        assertParticipant(conversation, requester.getId());

        return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                .map(message -> {
                    // SYSTEM / CONTACT_SHARE_UPDATE messages have no sender to resolve.
                    if (message.getSenderId() == null) {
                        return enrichMessage(message, null);
                    }
                    ProfileLookupService.ProfileSummary sender = profileLookupService.getSummary(message.getSenderId());
                    return enrichMessage(message, sender.name());
                });
    }

    public Page<ConversationResponse> getMyConversations(User user, Pageable pageable) {
        return conversationRepository.findByAuthorIdOrApplicantId(user.getId(), user.getId(), pageable)
                .map(this::enrichConversation);
    }

    private void assertParticipant(Conversation conversation, String userId) {
        if (!conversation.getAuthorId().equals(userId) && !conversation.getApplicantId().equals(userId)) {
            throw new ForbiddenException("You are not a participant in this conversation.");
        }
    }

    private void postSystemMessage(Conversation conversation, String content) {
        Message message = Message.builder()
                .conversationId(conversation.getId())
                .senderId(null)
                .type(MessageType.SYSTEM)
                .content(content)
                .build();
        messageRepository.save(message);
        touchConversation(conversation, content);
    }

    private void touchConversation(Conversation conversation, String preview) {
        conversation.setLastMessagePreview(preview);
        conversation.setLastMessageAt(Instant.now());
        conversationRepository.save(conversation);
    }

    private String describeConsent(com.example.eduspace.application.entity.Application application) {
        boolean phone = application.getContactShareConsent().isPhoneShared();
        boolean email = application.getContactShareConsent().isEmailShared();

        if (!phone && !email) return "The author has not shared contact details yet.";
        if (phone && email) return "The author shared their phone number and email.";
        return phone ? "The author shared their phone number." : "The author shared their email.";
    }

    private MessageResponse enrichMessage(Message message, String senderName) {
        MessageResponse response = mapper.toResponse(message);
        response.setSenderName(senderName);
        return response;
    }

    private ConversationResponse enrichConversation(Conversation conversation) {
        ConversationResponse response = mapper.toResponse(conversation);

        ProfileLookupService.ProfileSummary author = profileLookupService.getSummary(conversation.getAuthorId());
        response.setAuthorName(author.name());
        response.setAuthorAvatarUrl(author.avatarUrl());

        ProfileLookupService.ProfileSummary applicant = profileLookupService.getSummary(conversation.getApplicantId());
        response.setApplicantName(applicant.name());
        response.setApplicantAvatarUrl(applicant.avatarUrl());

        try {
            Opportunity opportunity = opportunityService.getEntity(conversation.getOpportunityId());
            response.setOpportunityTitle(opportunity.getTitle());
        } catch (ResourceNotFoundException ex) {
            response.setOpportunityTitle("Deleted posting");
        }

        return response;
    }
}