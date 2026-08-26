package com.example.eduspace.chat.websocket;

import java.time.Instant;

public record PresenceEvent(String userId, boolean online, Instant at) {}