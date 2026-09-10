package com.example.eduspace.post.dto.request;

import com.example.eduspace.post.enums.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {

    @NotNull(message = "Post type is required.")
    private PostType type;

    @Size(max = 5000, message = "Content is too long.")
    private String content;

    private List<String> mediaUrls;

    private String documentUrl;

    private String documentFileName;

    private List<@NotBlank @Size(max = 100) String> pollOptions;

    private boolean pollAllowMultipleChoice;

    private Instant pollExpiresAt;
}