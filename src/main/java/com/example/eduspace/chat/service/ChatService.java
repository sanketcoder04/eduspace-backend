package com.example.eduspace.chat.service;

import com.example.eduspace.application.entity.Application;
import com.example.eduspace.chat.dto.response.ConversationResponse;
import com.example.eduspace.chat.dto.response.MessageResponse;
import com.example.eduspace.chat.dto.ws.ReadReceiptEvent;
import com.example.eduspace.chat.dto.ws.TypingEvent;
import com.example.eduspace.chat.entity.Conversation;
import com.example.eduspace.chat.entity.Message;
import com.example.eduspace.chat.enums.ConversationStatus;
import com.example.eduspace.chat.enums.MessageType;
import com.example.eduspace.chat.mapper.ChatMapper;
import com.example.eduspace.chat.repository.ConversationRepository;
import com.example.eduspace.chat.repository.MessageRepository;
import com.example.eduspace.chat.websocket.PresenceService;
import com.example.eduspace.common.service.ProfileLookupService;
import com.example.eduspace.exception.BadRequestException;
import com.example.eduspace.exception.ForbiddenException;
import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.opportunity.entity.Opportunity;
import com.example.eduspace.opportunity.service.OpportunityService;
import com.example.eduspace.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepository;

    private final MessageRepository messageRepository;

    private final OpportunityService opportunityService;

    private final ChatMapper mapper;

    private final ProfileLookupService profileLookupService;

    private final SimpMessagingTemplate messagingTemplate;

    private final PresenceService presenceService;

    // NotificationService intentionally NOT used for NEW_MESSAGE anymore —
    // chat has its own unread-badge system now; the bell stays reserved for
    // application lifecycle events only.

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
        pushConversationPreviewToBoth(saved);

        presenceService.sendCurrentPresenceSnapshot(saved.getAuthorId());
        presenceService.sendCurrentPresenceSnapshot(saved.getApplicantId());
    }

    public void closeConversation(String applicationId) {
        conversationRepository.findByApplicationId(applicationId).ifPresent(conversation -> {
            conversation.setStatus(ConversationStatus.CLOSED);
            conversationRepository.save(conversation);
            postSystemMessage(conversation, "This application was closed. The chat is now read-only.");
        });
    }

    public void postContactShareUpdate(Application application) {
        conversationRepository.findByApplicationId(application.getId()).ifPresent(conversation -> {
            String summary = describeConsent(application);
            Message message = Message.builder()
                    .conversationId(conversation.getId())
                    .senderId(null)
                    .type(MessageType.CONTACT_SHARE_UPDATE)
                    .content(summary)
                    .build();

            Message saved = messageRepository.save(message);
            touchConversation(conversation, summary);
            broadcastMessage(conversation, enrichMessage(saved, null));
        });
    }

    public MessageResponse sendMessage(User sender, String conversationId, String content) {
        if (content == null || content.isBlank()) {
            throw new BadRequestException("Message content cannot be empty.");
        }

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

        MessageResponse response = enrichMessage(saved, sender.getName());
        broadcastMessage(conversation, response);
        pushConversationPreviewToBoth(conversation);

        // No NotificationService call here anymore — the recipient's unread
        // badge (on the Chat nav icon and the conversation list) is the
        // live signal for a new message now, not a Notification document.

        return response;
    }

    public void markRead(User reader, String conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found."));

        assertParticipant(conversation, reader.getId());

        var unread = messageRepository.findByConversationIdAndSenderIdNotAndReadFalse(conversationId, reader.getId());
        if (unread.isEmpty()) return;

        Instant now = Instant.now();
        unread.forEach(message -> {
            message.setRead(true);
            message.setReadAt(now);
        });
        messageRepository.saveAll(unread);

        String otherPartyId = conversation.getAuthorId().equals(reader.getId())
                ? conversation.getApplicantId()
                : conversation.getAuthorId();

        messagingTemplate.convertAndSendToUser(
                otherPartyId,
                "/queue/read-receipts",
                new ReadReceiptEvent(conversationId, reader.getId(), now)
        );

        // The reader's own unread badge for this conversation should drop
        // to zero immediately — push them a fresh conversation snapshot too.
        messagingTemplate.convertAndSendToUser(
                reader.getId(),
                "/queue/conversations",
                enrichConversation(conversation, reader.getId())
        );
    }

    public void notifyTyping(User user, String conversationId, boolean typing) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found."));

        assertParticipant(conversation, user.getId());

        String peerId = conversation.getAuthorId().equals(user.getId())
                ? conversation.getApplicantId()
                : conversation.getAuthorId();

        messagingTemplate.convertAndSendToUser(
                peerId,
                "/queue/typing",
                new TypingEvent(conversationId, user.getId(), typing)
        );
    }

    public Page<MessageResponse> getMessages(User requester, String conversationId, Pageable pageable) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found."));

        assertParticipant(conversation, requester.getId());

        return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                .map(message -> {
                    if (message.getSenderId() == null) return enrichMessage(message, null);
                    ProfileLookupService.ProfileSummary sender = profileLookupService.getSummary(message.getSenderId());
                    return enrichMessage(message, sender.name());
                });
    }

    public Page<ConversationResponse> getMyConversations(User user, Pageable pageable) {
        return conversationRepository.findByAuthorIdOrApplicantId(user.getId(), user.getId(), pageable)
                .map(conversation -> enrichConversation(conversation, user.getId()));
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

        Message saved = messageRepository.save(message);
        touchConversation(conversation, content);
        broadcastMessage(conversation, enrichMessage(saved, null));
    }

    private void touchConversation(Conversation conversation, String preview) {
        conversation.setLastMessagePreview(preview);
        conversation.setLastMessageAt(Instant.now());
        conversationRepository.save(conversation);
    }

    private void broadcastMessage(Conversation conversation, MessageResponse response) {
        messagingTemplate.convertAndSendToUser(conversation.getAuthorId(), "/queue/messages", response);
        messagingTemplate.convertAndSendToUser(conversation.getApplicantId(), "/queue/messages", response);
    }

    /** unreadCount differs per viewer, so author and applicant each get their OWN snapshot, not a shared broadcast. */
    private void pushConversationPreviewToBoth(Conversation conversation) {
        messagingTemplate.convertAndSendToUser(
                conversation.getAuthorId(), "/queue/conversations", enrichConversation(conversation, conversation.getAuthorId()));
        messagingTemplate.convertAndSendToUser(
                conversation.getApplicantId(), "/queue/conversations", enrichConversation(conversation, conversation.getApplicantId()));
    }

    private String describeConsent(Application application) {
        boolean phone = application.getContactShareConsent().isPhoneShared();
        boolean email = application.getContactShareConsent().isEmailShared();

        if (!phone && !email) return "The author has not shared contact details yet.";

        ProfileLookupService.ContactInfo contact = profileLookupService.getContactInfo(application.getAuthorId());
        StringBuilder message = new StringBuilder("The author shared their ");

        if (phone && contact.phoneNumber() != null) {
            message.append("phone number (").append(contact.phoneNumber()).append(")");
        }
        if (phone && email && contact.phoneNumber() != null && contact.email() != null) {
            message.append(" and ");
        }
        if (email && contact.email() != null) {
            message.append("email (").append(contact.email()).append(")");
        }
        message.append(".");

        return message.toString();
    }

    private MessageResponse enrichMessage(Message message, String senderName) {
        MessageResponse response = mapper.toResponse(message);
        response.setSenderName(senderName);
        return response;
    }

    private ConversationResponse enrichConversation(Conversation conversation, String viewerId) {
        ConversationResponse response = mapper.toResponse(conversation);

        ProfileLookupService.ProfileSummary author = profileLookupService.getSummary(conversation.getAuthorId());
        response.setAuthorName(author.name());
        response.setAuthorAvatarUrl(author.avatarUrl());

        ProfileLookupService.ProfileSummary applicant = profileLookupService.getSummary(conversation.getApplicantId());
        response.setApplicantName(applicant.name());
        response.setApplicantAvatarUrl(applicant.avatarUrl());

        response.setUnreadCount(
                (int) messageRepository.countByConversationIdAndSenderIdNotAndReadFalse(conversation.getId(), viewerId)
        );

        try {
            Opportunity opportunity = opportunityService.getEntity(conversation.getOpportunityId());
            response.setOpportunityTitle(opportunity.getTitle());
        } catch (ResourceNotFoundException ex) {
            response.setOpportunityTitle("Deleted posting");
        }

        return response;
    }
}