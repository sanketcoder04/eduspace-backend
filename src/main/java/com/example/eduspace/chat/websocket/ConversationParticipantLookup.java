package com.example.eduspace.chat.websocket;

import com.example.eduspace.chat.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ConversationParticipantLookup {

    private final ConversationRepository conversationRepository;

    /** Everyone this user shares an active-or-past conversation with — presence is scoped to these people only. */
    public Set<String> findConversationPeers(String userId) {
        Set<String> peers = new HashSet<>();

        conversationRepository.findByAuthorIdOrApplicantId(userId, userId, Pageable.unpaged())
                .forEach(conversation -> {
                    String peerId = conversation.getAuthorId().equals(userId)
                            ? conversation.getApplicantId()
                            : conversation.getAuthorId();
                    peers.add(peerId);
                });
        return peers;
    }
}