package com.example.eduspace.auth.dto.request;

import com.example.eduspace.common.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteGoogleRegistrationRequest {

    @NotBlank(message = "Registration token is required.")
    private String registrationToken;

    @NotNull(message = "Role is required.")
    private Role role;
}