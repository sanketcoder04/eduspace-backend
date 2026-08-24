package com.example.eduspace.notification.service;

import com.example.eduspace.exception.ForbiddenException;
import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.notification.dto.response.NotificationResponse;
import com.example.eduspace.notification.entity.Notification;
import com.example.eduspace.notification.enums.NotificationType;
import com.example.eduspace.notification.mapper.NotificationMapper;
import com.example.eduspace.notification.repository.NotificationRepository;
import com.example.eduspace.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final NotificationMapper mapper;

    /** Internal fire-and-forget call from other services — no DTO needed here. */
    public void notify(String recipientId, NotificationType type, String title, String body, String referenceType, String referenceId) {
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .type(type)
                .title(title)
                .body(body)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    public Page<NotificationResponse> getForUser(User user, Pageable pageable) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(mapper::toResponse);
    }

    public long getUnreadCount(User user) {
        return notificationRepository.countByRecipientIdAndReadFalse(user.getId());
    }

    public NotificationResponse markRead(User user, String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found."));

        if (!notification.getRecipientId().equals(user.getId())) {
            throw new ForbiddenException("This notification does not belong to you.");
        }

        notification.setRead(true);
        notification.setReadAt(Instant.now());
        return mapper.toResponse(notificationRepository.save(notification));
    }

    public void markAllRead(User user) {
        notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId(), Pageable.unpaged())
                .forEach(n -> {
                    if (!n.isRead()) {
                        n.setRead(true);
                        n.setReadAt(Instant.now());
                        notificationRepository.save(n);
                    }
                });
    }
}