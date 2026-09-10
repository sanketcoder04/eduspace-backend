package com.example.eduspace.post.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequest {

    @NotEmpty(message = "Select at least one option.")
    private List<String> selectedOptionIds;
}