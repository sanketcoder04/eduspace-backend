package com.example.eduspace.chat.controller;

import com.example.eduspace.chat.dto.request.SendMessageRequest;
import com.example.eduspace.chat.dto.response.ConversationResponse;
import com.example.eduspace.chat.dto.response.MessageResponse;
import com.example.eduspace.chat.service.ChatService;
import com.example.eduspace.common.dto.ApiResponse;
import com.example.eduspace.security.authentication.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ConversationResponse>>> getMyConversations(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ConversationResponse> myConversations = chatService.getMyConversations(userDetails.user(), pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<ConversationResponse>>builder()
                        .success(true)
                        .message("Conversations fetched.")
                        .data(myConversations)
                        .build()
        );
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getMessages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @PageableDefault(size = 30) Pageable pageable) {

        Page<MessageResponse> messages = chatService.getMessages(userDetails.user(), id, pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<MessageResponse>>builder()
                        .success(true)
                        .message("Messages fetched.")
                        .data(messages)
                        .build()
        );
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody SendMessageRequest request) {

        MessageResponse message = chatService.sendMessage(userDetails.user(), id, request.getContent());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<MessageResponse>builder()
                        .success(true)
                        .message("Message sent.")
                        .data(message)
                        .build()
        );
    }
}