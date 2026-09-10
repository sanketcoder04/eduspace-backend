package com.example.eduspace.post.service;

import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.notification.enums.NotificationType;
import com.example.eduspace.notification.service.NotificationService;
import com.example.eduspace.post.entity.Comment;
import com.example.eduspace.post.entity.Like;
import com.example.eduspace.post.entity.Post;
import com.example.eduspace.post.enums.LikeTargetType;
import com.example.eduspace.post.repository.CommentRepository;
import com.example.eduspace.post.repository.LikeRepository;
import com.example.eduspace.post.repository.PostRepository;
import com.example.eduspace.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

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
}