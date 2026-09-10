package com.example.eduspace.post.util;

import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts mentioned userIds from editor-produced HTML. The frontend's rich
 * text editor renders a mention as a span carrying the target userId in a
 * data attribute — e.g. <span data-mention-id="65f...">@Jane Doe</span> —
 * so parsing is a straightforward regex over that one attribute, no HTML
 * parser dependency needed.
 */
@NoArgsConstructor
public final class MentionParser {

    private static final Pattern MENTION_PATTERN = Pattern.compile("data-mention-id=\"([a-fA-F0-9]{24})\"");

    public static List<String> extractMentionedUserIds(String html) {
        if (html == null || html.isBlank()) return List.of();

        var ids = new LinkedHashSet<String>(); // LinkedHashSet: de-dupes but preserves first-mention order

        Matcher matcher = MENTION_PATTERN.matcher(html);

        while (matcher.find()) {
            ids.add(matcher.group(1));
        }
        return List.copyOf(ids);
    }
}