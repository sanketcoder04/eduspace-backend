package com.example.eduspace.chat.mapper;

import com.example.eduspace.chat.dto.response.ConversationResponse;
import com.example.eduspace.chat.dto.response.MessageResponse;
import com.example.eduspace.chat.entity.Conversation;
import com.example.eduspace.chat.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ChatMapper {

    @Mapping(target = "opportunityTitle", ignore = true)
    @Mapping(target = "authorName", ignore = true)
    @Mapping(target = "authorAvatarUrl", ignore = true)
    @Mapping(target = "applicantName", ignore = true)
    @Mapping(target = "applicantAvatarUrl", ignore = true)
    ConversationResponse toResponse(Conversation conversation);

    List<ConversationResponse> toResponseList(List<Conversation> conversations);

    @Mapping(target = "senderName", ignore = true)
    MessageResponse toResponse(Message message);

    List<MessageResponse> toMessageResponseList(List<Message> messages);
}