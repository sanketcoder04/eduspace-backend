package com.example.eduspace.chat.websocket;

import com.example.eduspace.security.authentication.CustomUserDetails;
import com.example.eduspace.security.authentication.CustomUserDetailsService;
import com.example.eduspace.security.jwt.JwtService;
import com.example.eduspace.security.jwt.JwtTokenType;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * Native WebSocket handshakes can't carry a custom Authorization header from
 * the browser, so auth is deferred to the STOMP CONNECT frame instead — its
 * native headers work identically over native WS and the SockJS fallback.
 * Mirrors JwtAuthenticationFilter's validation (same JwtService calls, same
 * "reject the ACCESS-only rule"), just attaching a Principal instead of a
 * SecurityContext, since Spring Security's context doesn't span WS sessions.
 */

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        String authHeader = accessor.getFirstNativeHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing bearer token on STOMP CONNECT frame.");
        }

        String token = authHeader.substring(7);

        try {
            if (jwtService.extractTokenType(token) != JwtTokenType.ACCESS) {
                throw new IllegalArgumentException("Only an access token may open a chat session.");
            }

            String username = jwtService.extractUsername(token);
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

            if (!jwtService.isTokenValid(token, userDetails)) {
                throw new IllegalArgumentException("Token is invalid or expired.");
            }

            accessor.setUser(new StompPrincipal(userDetails.user().getId()));

        } catch (JwtException ex) {
            throw new IllegalArgumentException("Invalid or expired token.", ex);
        }
        return message;
    }
}