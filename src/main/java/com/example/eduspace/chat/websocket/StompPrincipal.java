package com.example.eduspace.chat.websocket;

import java.security.Principal;

/**
 * Wraps the authenticated user's Mongo id as the STOMP session Principal, so
 * Simp MessagingTemplate#convertAndSendToUser(userId, ...) resolves correctly —
 * Conversation.authorId/applicantId are stored as this exact same id.
 */

public record StompPrincipal(String name) implements Principal {

    @Override
    public String getName() {
        return name;
    }
}