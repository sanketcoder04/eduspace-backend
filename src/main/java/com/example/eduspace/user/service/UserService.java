package com.example.eduspace.user.service;

import com.example.eduspace.common.service.ProfileLookupService;
import com.example.eduspace.exception.ResourceNotFoundException;
import com.example.eduspace.user.dto.response.UserSummaryResponse;
import com.example.eduspace.user.entity.User;
import com.example.eduspace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    /** `id` is the unique tie-breaker that keeps offset pagination stable across pages. */
    private static final Sort SEARCH_SORT = Sort.by("name").and(Sort.by("id"));

    private final UserRepository userRepository;

    private final ProfileLookupService profileLookupService;

    public UserSummaryResponse getSummary(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        String avatarUrl = profileLookupService.findProfileSummary(user)
                .map(ProfileLookupService.ProfileSummary::avatarUrl)
                .orElse(null);

        return toResponse(user, avatarUrl);
    }

    public Slice<UserSummaryResponse> searchUsersPage(String query, String excludeUserId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, SEARCH_SORT);

        if (query == null || query.isBlank()) {
            return new SliceImpl<>(List.of(), pageable, false);
        }

        Slice<User> matches = userRepository.findByNameContainingIgnoreCaseAndIdNot(
                query.trim(), excludeUserId, pageable);

        var summaries = profileLookupService.findProfileSummaries(matches.getContent());

        List<UserSummaryResponse> content = matches.getContent().stream()
                .filter(user -> summaries.containsKey(user.getId()))
                .map(user -> toResponse(user, summaries.get(user.getId()).avatarUrl()))
                .toList();

        return new SliceImpl<>(content, pageable, matches.hasNext());
    }

    public List<UserSummaryResponse> searchUsers(String query, String excludeUserId, int limit) {
        return searchUsersPage(query, excludeUserId, 0, limit).getContent();
    }

    private UserSummaryResponse toResponse(User user, String avatarUrl) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .role(user.getRole())
                .avatarUrl(avatarUrl)
                .build();
    }
}