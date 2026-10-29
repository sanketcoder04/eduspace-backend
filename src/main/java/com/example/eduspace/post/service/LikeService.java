package com.example.eduspace.post.service;

import com.example.eduspace.common.dto.SliceResponse;
import com.example.eduspace.common.service.ProfileLookupService;
import com.example.eduspace.exception.BadRequestException;
import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.notification.enums.NotificationType;
import com.example.eduspace.notification.service.NotificationService;
import com.example.eduspace.post.dto.response.LikerResponse;
import com.example.eduspace.post.entity.Comment;
import com.example.eduspace.post.entity.Like;
import com.example.eduspace.post.entity.Post;
import com.example.eduspace.post.enums.LikeTargetType;
import com.example.eduspace.post.repository.CommentRepository;
import com.example.eduspace.post.repository.LikeRepository;
import com.example.eduspace.post.repository.PostRepository;
import com.example.eduspace.user.entity.User;
import com.example.eduspace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private static final int LIKERS_DEFAULT_PAGE_SIZE = 20;

    private static final int LIKERS_MAX_PAGE_SIZE = 50;

    private static final Sort LIKERS_SORT = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));

    private static final int LIKERS_MAX_QUERY_LENGTH = 100;

    private final UserRepository userRepository;

    private final ProfileLookupService profileLookupService;

    private final LikeRepository likeRepository;

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final NotificationService notificationService;

    public boolean toggleLike(User user, LikeTargetType targetType, String targetId) {
        var existing = likeRepository.findByTargetTypeAndTargetIdAndUserId(targetType, targetId, user.getId());

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            adjustCount(targetType, targetId, -1);
            return false; // now unliked
        }

        likeRepository.save(Like.builder().targetType(targetType).targetId(targetId).userId(user.getId()).build());
        adjustCount(targetType, targetId, +1);
        notifyOwner(user, targetType, targetId);
        return true; // now liked
    }

    private void adjustCount(LikeTargetType targetType, String targetId, int delta) {
        if (targetType == LikeTargetType.POST) {
            Post post = postRepository.findById(targetId)
                    .orElseThrow(() -> new ResourceNotFoundException("Post not found."));
            post.setLikesCount(Math.max(0, post.getLikesCount() + delta));
            postRepository.save(post);
        } else {
            Comment comment = commentRepository.findById(targetId)
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found."));
            comment.setLikesCount(Math.max(0, comment.getLikesCount() + delta));
            commentRepository.save(comment);
        }
    }

    private void notifyOwner(User liker, LikeTargetType targetType, String targetId) {
        String ownerId;
        String referenceType;

        if (targetType == LikeTargetType.POST) {
            Post post = postRepository.findById(targetId).orElse(null);
            if (post == null || post.getAuthorId().equals(liker.getId())) return; // no self-notify
            ownerId = post.getAuthorId();
            referenceType = "POST";
        } else {
            Comment comment = commentRepository.findById(targetId).orElse(null);
            if (comment == null || comment.getAuthorId().equals(liker.getId())) return;
            ownerId = comment.getAuthorId();
            referenceType = "COMMENT";
        }
        notificationService.notify(
                ownerId,
                NotificationType.POST_LIKED,
                "New like",
                liker.getName() + " liked your " + referenceType.toLowerCase() + ".",
                referenceType,
                targetId
        );
    }

    public SliceResponse<LikerResponse> getPostLikers(String postId, String query, int page, Integer requestedSize) {
        String trimmed = query == null ? "" : query.trim();
        if (trimmed.length() > LIKERS_MAX_QUERY_LENGTH) {
            throw new BadRequestException("Search text is too long.");
        }

        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found.");
        }

        int size = requestedSize == null
                ? LIKERS_DEFAULT_PAGE_SIZE
                : Math.min(Math.max(requestedSize, 1), LIKERS_MAX_PAGE_SIZE);
        int safePage = Math.max(page, 0);

        Slice<Like> likes = trimmed.isEmpty()
                ? likeRepository.findByTargetTypeAndTargetId(
                LikeTargetType.POST, postId, PageRequest.of(safePage, size, LIKERS_SORT))
                : likeRepository.searchPostLikersByName(postId, trimmed, PageRequest.of(safePage, size));

        if (!likes.hasContent()) {
            return new SliceResponse<>(List.of(), safePage, size, likes.hasNext());
        }

        List<String> userIds = likes.getContent().stream().map(Like::getUserId).toList();
        Map<String, User> usersById = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        var summaries = profileLookupService.findProfileSummaries(usersById.values());

        List<LikerResponse> content = likes.getContent().stream()
                .filter(like -> usersById.containsKey(like.getUserId()))
                .map(like -> {
                    User user = usersById.get(like.getUserId());
                    var summary = summaries.get(user.getId());
                    return LikerResponse.builder()
                            .userId(user.getId())
                            .name(user.getName())
                            .avatarUrl(summary == null ? null : summary.avatarUrl())
                            .role(user.getRole())
                            .likedAt(like.getCreatedAt())
                            .build();
                })
                .toList();

        return new SliceResponse<>(content, safePage, size, likes.hasNext());
    }
}