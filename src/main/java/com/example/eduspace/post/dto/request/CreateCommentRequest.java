package com.example.eduspace.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {

    @NotBlank(message = "Comment content is required.")
    @Size(max = 2000, message = "Comment is too long.")
    private String content;

    private String parentCommentId;
}