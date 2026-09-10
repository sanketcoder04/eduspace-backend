package com.example.eduspace.post.service;

import com.example.eduspace.common.service.ProfileLookupService;
import com.example.eduspace.exception.BadRequestException;
import com.example.eduspace.exception.ForbiddenException;
import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.notification.enums.NotificationType;
import com.example.eduspace.notification.service.NotificationService;
import com.example.eduspace.post.dto.response.CommentResponse;
import com.example.eduspace.post.entity.Comment;
import com.example.eduspace.post.entity.Post;
import com.example.eduspace.post.enums.LikeTargetType;
import com.example.eduspace.post.mapper.PostMapper;
import com.example.eduspace.post.repository.CommentRepository;
import com.example.eduspace.post.repository.LikeRepository;
import com.example.eduspace.post.repository.PostRepository;
import com.example.eduspace.post.util.HtmlSanitizer;
import com.example.eduspace.post.util.MentionParser;
import com.example.eduspace.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final LikeRepository likeRepository;

    private final NotificationService notificationService;

    private final ProfileLookupService profileLookupService;

    private final HtmlSanitizer htmlSanitizer;

    private final PostMapper mapper;

    public CommentResponse addComment(User author, String postId, String content, String parentCommentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found."));

        String sanitizedContent = htmlSanitizer.sanitize(content);
        if (!htmlSanitizer.hasVisibleText(sanitizedContent)) {
            throw new BadRequestException("Comment cannot be empty.");
        }

        List<String> mentions = MentionParser.extractMentionedUserIds(sanitizedContent);

        Comment comment = Comment.builder()
                .postId(postId)
                .authorId(author.getId())
                .content(sanitizedContent)
                .mentions(mentions)
                .parentCommentId(parentCommentId)
                .likesCount(0)
                .edited(false)
                .build();

        Comment saved = commentRepository.save(comment);

        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        if (!post.getAuthorId().equals(author.getId())) {
            notificationService.notify(
                    post.getAuthorId(),
                    NotificationType.POST_COMMENTED,
                    "New comment",
                    author.getName() + " commented on your post.",
                    "POST",
                    postId
            );
        }

        for (String mentionedUserId : mentions) {
            if (mentionedUserId.equals(author.getId())) continue;
            notificationService.notify(
                    mentionedUserId,
                    NotificationType.COMMENT_MENTION,
                    "You were mentioned",
                    author.getName() + " mentioned you in a comment.",
                    "COMMENT",
                    saved.getId()
            );
        }

        return enrich(saved, author.getId());
    }

    public Page<CommentResponse> getTopLevelComments(User viewer, String postId, Pageable pageable) {
        return commentRepository.findByPostIdAndParentCommentIdIsNullOrderByCreatedAtDesc(postId, pageable)
                .map(comment -> enrich(comment, viewer.getId()));
    }

    public Page<CommentResponse> getReplies(User viewer, String parentCommentId, Pageable pageable) {
        return commentRepository.findByParentCommentIdOrderByCreatedAtAsc(parentCommentId, pageable)
                .map(comment -> enrich(comment, viewer.getId()));
    }

    public void deleteComment(User author, String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found."));

        if (!comment.getAuthorId().equals(author.getId())) {
            throw new ForbiddenException("You do not own this comment.");
        }

        commentRepository.deleteById(commentId);

        postRepository.findById(comment.getPostId()).ifPresent(post -> {
            post.setCommentsCount(Math.max(0, post.getCommentsCount() - 1));
            postRepository.save(post);
        });
    }

    private CommentResponse enrich(Comment comment, String viewerId) {
        CommentResponse response = mapper.toResponse(comment);

        ProfileLookupService.ProfileSummary author = profileLookupService.getSummary(comment.getAuthorId());
        response.setAuthorName(author.name());
        response.setAuthorAvatarUrl(author.avatarUrl());

        response.setLikedByViewer(
                likeRepository.findByTargetTypeAndTargetIdAndUserId(LikeTargetType.COMMENT, comment.getId(), viewerId).isPresent()
        );

        // 0 for a reply itself (replies aren't threaded further) — harmless
        // to compute unconditionally rather than branching on parentCommentId.
        response.setRepliesCount(commentRepository.countByParentCommentId(comment.getId()));

        return response;
    }
}