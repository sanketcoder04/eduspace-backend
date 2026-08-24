package com.example.eduspace.application.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactShareConsentRequest {

    private boolean phoneShared;

    private boolean emailShared;
}