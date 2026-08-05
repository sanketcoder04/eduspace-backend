package com.example.eduspace.auth.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleAuthResponse {

    private GoogleAuthStatus status;

    private AuthResponse auth;

    private String registrationToken;

    private String email;

    private String name;
}