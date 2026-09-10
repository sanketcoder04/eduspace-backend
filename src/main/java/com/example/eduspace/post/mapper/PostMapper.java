package com.example.eduspace.post.mapper;

import com.example.eduspace.post.dto.response.*;
import com.example.eduspace.post.entity.Comment;
import com.example.eduspace.post.entity.PollData;
import com.example.eduspace.post.entity.PollOption;
import com.example.eduspace.post.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PostMapper {

    @Mapping(target = "authorName", ignore = true)
    @Mapping(target = "authorAvatarUrl", ignore = true)
    @Mapping(target = "likedByViewer", ignore = true)
    @Mapping(target = "viewerSelectedOptionIds", ignore = true)
    PostResponse toResponse(Post post);

    PollDataResponse toPollDataResponse(PollData pollData);

    PollOptionResponse toPollOptionResponse(PollOption pollOption);

    List<PollOptionResponse> toPollOptionResponseList(List<PollOption> options);

    @Mapping(target = "authorName", ignore = true)
    @Mapping(target = "authorAvatarUrl", ignore = true)
    @Mapping(target = "likedByViewer", ignore = true)
    @Mapping(target = "repliesCount", ignore = true)
    CommentResponse toResponse(Comment comment);
}