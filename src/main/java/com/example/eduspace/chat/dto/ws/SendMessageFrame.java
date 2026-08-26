package com.example.eduspace.chat.dto.ws;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageFrame {

    private String content;
}