package com.example.eduspace.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    @NotBlank
    private String content;
}