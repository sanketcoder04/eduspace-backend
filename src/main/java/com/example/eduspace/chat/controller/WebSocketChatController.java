package com.example.eduspace.chat.controller;

import com.example.eduspace.chat.dto.ws.SendMessageFrame;
import com.example.eduspace.chat.dto.ws.TypingFrame;
import com.example.eduspace.chat.dto.ws.WsErrorEvent;
import com.example.eduspace.chat.service.ChatService;
import com.example.eduspace.chat.websocket.PresenceService;
import com.example.eduspace.user.entity.User;
import com.example.eduspace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final ChatService chatService;

    private final UserRepository userRepository;

    private final PresenceService presenceService;

    // Client publishes to: /app/conversations/{conversationId}/send
    @MessageMapping("/conversations/{conversationId}/send")
    public void send(@DestinationVariable String conversationId, @Payload SendMessageFrame frame, Principal principal) {
        chatService.sendMessage(resolveUser(principal), conversationId, frame.getContent());
    }

    // Client publishes to: /app/conversations/{conversationId}/typing
    @MessageMapping("/conversations/{conversationId}/typing")
    public void typing(@DestinationVariable String conversationId, @Payload TypingFrame frame, Principal principal) {
        chatService.notifyTyping(resolveUser(principal), conversationId, frame.isTyping());
    }

    // Client publishes to: /app/conversations/{conversationId}/read
    @MessageMapping("/conversations/{conversationId}/read")
    public void read(@DestinationVariable String conversationId, Principal principal) {
        chatService.markRead(resolveUser(principal), conversationId);
    }

    /** Sends a validation/authorization error back only to the sender, on /user/queue/errors. */
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public WsErrorEvent handleException(Exception ex) {
        return WsErrorEvent.builder().error(ex.getMessage()).build();
    }

    private User resolveUser(Principal principal) {
        return userRepository.findById(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user no longer exists."));
    }

    @MessageMapping("/presence/refresh")
    public void refreshPresence(Principal principal) {
        presenceService.sendCurrentPresenceSnapshot(principal.getName());
    }
}