package com.example.eduspace.chat.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class PresenceService {

    private final SimpMessagingTemplate messagingTemplate;

    private final ConversationParticipantLookup participantLookup;

    // userId -> open session count (a user can have multiple tabs/devices connected at once).
    private final Map<String, Integer> activeSessionCounts = new ConcurrentHashMap<>();

    private final Map<String, Instant> lastSeenAt = new ConcurrentHashMap<>();

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        Principal principal = event.getUser();
        if (principal == null) return;

        String userId = principal.getName();
        boolean wasOffline = activeSessionCounts.merge(userId, 1, Integer::sum) == 1;

        if (wasOffline) {
            broadcastPresence(userId, true);
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        Principal principal = event.getUser();
        if (principal == null) return;

        String userId = principal.getName();
        Integer remaining = activeSessionCounts.computeIfPresent(userId,
                (id, count) -> count - 1 <= 0 ? null : count - 1);

        if (remaining == null) {
            lastSeenAt.put(userId, Instant.now());
            broadcastPresence(userId, false);
        }
    }

    public boolean isOnline(String userId) {
        return activeSessionCounts.containsKey(userId);
    }

    public Instant getLastSeenAt(String userId) {
        return lastSeenAt.get(userId);
    }

    private void broadcastPresence(String userId, boolean online) {
        Set<String> peers = participantLookup.findConversationPeers(userId);
        PresenceEvent payload = new PresenceEvent(userId, online, Instant.now());

        for (String peerId : peers) {
            messagingTemplate.convertAndSendToUser(peerId, "/queue/presence", payload);
        }
    }
}