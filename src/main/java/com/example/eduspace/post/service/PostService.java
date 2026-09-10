package com.example.eduspace.post.service;

import com.example.eduspace.common.service.ProfileLookupService;
import com.example.eduspace.exception.BadRequestException;
import com.example.eduspace.exception.ForbiddenException;
import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.follow.repository.FollowRepository;
import com.example.eduspace.notification.enums.NotificationType;
import com.example.eduspace.notification.service.NotificationService;
import com.example.eduspace.post.dto.request.CreatePostRequest;
import com.example.eduspace.post.dto.response.PostResponse;
import com.example.eduspace.post.entity.*;
import com.example.eduspace.post.enums.LikeTargetType;
import com.example.eduspace.post.enums.PostType;
import com.example.eduspace.post.mapper.PostMapper;
import com.example.eduspace.post.repository.LikeRepository;
import com.example.eduspace.post.repository.PollVoteRepository;
import com.example.eduspace.post.repository.PostRepository;
import com.example.eduspace.post.util.HtmlSanitizer;
import com.example.eduspace.post.util.MentionParser;
import com.example.eduspace.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final LikeRepository likeRepository;

    private final PollVoteRepository pollVoteRepository;

    private final FollowRepository followRepository;

    private final NotificationService notificationService;

    private final ProfileLookupService profileLookupService;

    private final HtmlSanitizer htmlSanitizer;

    private final PostMapper mapper;

    public PostResponse createPost(User author, CreatePostRequest request) {
        String sanitizedContent = htmlSanitizer.sanitize(request.getContent());
        validateContentForType(request, sanitizedContent);

        List<String> mentions = MentionParser.extractMentionedUserIds(sanitizedContent);

        Post post = Post.builder()
                .authorId(author.getId())
                .authorRole(author.getRole())
                .type(request.getType())
                .content(sanitizedContent)
                .mediaUrls(request.getMediaUrls())
                .documentUrl(request.getDocumentUrl())
                .documentFileName(request.getDocumentFileName())
                .mentions(mentions)
                .likesCount(0)
                .commentsCount(0)
                .edited(false)
                .build();

        if (request.getType() == PostType.POLL) {
            post.setPoll(buildPollData(request));
        }

        Post saved = postRepository.save(post);

        notifyMentions(author, mentions, saved.getId(), "POST");

        return enrich(saved, author.getId());
    }

    /** Personalized feed: the author's own posts + everyone they follow, newest first — pure chronological for MVP, no ranking algorithm. */
    public Page<PostResponse> getFeed(User viewer, Pageable pageable) {
        List<String> followingIds = followRepository.findByFollowerId(viewer.getId()).stream()
                .map(f -> f.getFollowingId())
                .collect(Collectors.toList());
        followingIds.add(viewer.getId());

        return postRepository.findByAuthorIdIn(followingIds, pageable)
                .map(post -> enrich(post, viewer.getId()));
    }

    public Page<PostResponse> getPostsByUser(User viewer, String authorId, Pageable pageable) {
        return postRepository.findByAuthorId(authorId, pageable)
                .map(post -> enrich(post, viewer.getId()));
    }

    public PostResponse getById(User viewer, String postId) {
        return enrich(getEntity(postId), viewer.getId());
    }

    public void deletePost(User author, String postId) {
        Post post = getEntity(postId);
        if (!post.getAuthorId().equals(author.getId())) {
            throw new ForbiddenException("You do not own this post.");
        }
        postRepository.deleteById(postId);
        // Comments/Likes/PollVotes are left as orphaned-but-harmless records
        // rather than adding delete fan-out — revisit with a scheduled
        // cleanup job if storage becomes a real concern.
    }

    public PostResponse voteOnPoll(User voter, String postId, List<String> selectedOptionIds) {
        Post post = getEntity(postId);

        if (post.getType() != PostType.POLL || post.getPoll() == null) {
            throw new BadRequestException("This post is not a poll.");
        }
        if (post.getPoll().getExpiresAt() != null && post.getPoll().getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("This poll has closed.");
        }
        if (pollVoteRepository.findByPostIdAndUserId(postId, voter.getId()).isPresent()) {
            throw new BadRequestException("You have already voted on this poll.");
        }
        if (!post.getPoll().isAllowMultipleChoice() && selectedOptionIds.size() > 1) {
            throw new BadRequestException("This poll only allows one selection.");
        }

        pollVoteRepository.save(PollVote.builder()
                .postId(postId)
                .userId(voter.getId())
                .selectedOptionIds(selectedOptionIds)
                .build());

        post.getPoll().getOptions().forEach(option -> {
            if (selectedOptionIds.contains(option.getId())) {
                option.setVotesCount(option.getVotesCount() + 1);
            }
        });
        post.getPoll().setTotalVotes(post.getPoll().getTotalVotes() + 1);

        Post saved = postRepository.save(post);
        return enrich(saved, voter.getId());
    }

    public Post getEntity(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found."));
    }

    public void save(Post post) {
        postRepository.save(post);
    }

    private void validateContentForType(CreatePostRequest request, String sanitizedContent) {
        switch (request.getType()) {
            case TEXT -> {
                if (!htmlSanitizer.hasVisibleText(sanitizedContent)) {
                    throw new BadRequestException("Text posts require content.");
                }
            }
            case IMAGE, VIDEO -> {
                if (request.getMediaUrls() == null || request.getMediaUrls().isEmpty()) {
                    throw new BadRequestException("At least one media file is required.");
                }
            }
            case DOCUMENT -> {
                if (request.getDocumentUrl() == null) {
                    throw new BadRequestException("A document file is required.");
                }
            }
            case POLL -> {
                if (request.getPollOptions() == null || request.getPollOptions().size() < 2) {
                    throw new BadRequestException("A poll needs at least two options.");
                }
            }
        }
    }

    private PollData buildPollData(CreatePostRequest request) {
        List<PollOption> options = request.getPollOptions().stream()
                .map(text -> PollOption.builder().id(UUID.randomUUID().toString()).text(text).votesCount(0).build())
                .toList();

        return PollData.builder()
                .options(options)
                .allowMultipleChoice(request.isPollAllowMultipleChoice())
                .expiresAt(request.getPollExpiresAt())
                .totalVotes(0)
                .build();
    }

    private void notifyMentions(User author, List<String> mentionedUserIds, String referenceId, String referenceType) {
        for (String mentionedUserId : mentionedUserIds) {
            if (mentionedUserId.equals(author.getId())) continue;
            notificationService.notify(
                    mentionedUserId,
                    referenceType.equals("POST") ? NotificationType.POST_MENTION : NotificationType.COMMENT_MENTION,
                    "You were mentioned",
                    author.getName() + " mentioned you in a " + (referenceType.equals("POST") ? "post" : "comment") + ".",
                    referenceType,
                    referenceId
            );
        }
    }

    private PostResponse enrich(Post post, String viewerId) {
        PostResponse response = mapper.toResponse(post);

        ProfileLookupService.ProfileSummary author = profileLookupService.getSummary(post.getAuthorId());
        response.setAuthorName(author.name());
        response.setAuthorAvatarUrl(author.avatarUrl());

        response.setLikedByViewer(
                likeRepository.findByTargetTypeAndTargetIdAndUserId(LikeTargetType.POST, post.getId(), viewerId).isPresent()
        );

        if (post.getType() == PostType.POLL && response.getPoll() != null) {
            pollVoteRepository.findByPostIdAndUserId(post.getId(), viewerId)
                    .ifPresent(vote -> response.setViewerSelectedOptionIds(vote.getSelectedOptionIds()));

            boolean expired = post.getPoll().getExpiresAt() != null
                    && post.getPoll().getExpiresAt().isBefore(Instant.now());
            response.getPoll().setExpired(expired);

            int total = response.getPoll().getTotalVotes();
            response.getPoll().getOptions().forEach(option -> {
                double percentage = total == 0 ? 0.0 : (option.getVotesCount() * 100.0) / total;
                option.setVotePercentage(Math.round(percentage * 10) / 10.0);
            });
        }
        return response;
    }
}