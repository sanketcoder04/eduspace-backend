package com.example.eduspace.post.util;

import org.owasp.html.AttributePolicy;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Strict ALLOWLIST (not blocklist) sanitizer for post/comment rich text.
 * The allowed tag set is deliberately exactly what the frontend's Tiptap
 * editor is configured to produce — bold/italic/underline, lists, and
 * mention spans — so nothing needs enumerating as "dangerous"; anything
 * Tiptap doesn't emit (scripts, iframes, event-handler attributes, style
 * attributes, arbitrary tags) is stripped by construction. Emojis need no
 * handling at all since they're plain Unicode text content, not markup.
 */
@Component
public class HtmlSanitizer {

    // Mirrors MentionParser's expected id shape — a Mongo ObjectId. Locking
    // the ALLOWED VALUE FORMAT down here (not just the attribute name) means
    // a malicious payload can't be smuggled through the mention attribute
    // itself; anything not matching this exact shape gets the attribute dropped.
    private static final Pattern OBJECT_ID = Pattern.compile("^[a-fA-F0-9]{24}$");

    private static final AttributePolicy MENTION_ID_POLICY =
            (elementName, attributeName, value) -> OBJECT_ID.matcher(value).matches() ? value : null;

    private static final PolicyFactory POLICY = new HtmlPolicyBuilder()
            .allowElements("p", "br", "b", "strong", "i", "em", "u", "ul", "ol", "li", "span")
            .allowAttributes("data-mention-id").matching(MENTION_ID_POLICY).onElements("span")
            .allowAttributes("class").onElements("span") // Tiptap's mention extension tags its span with a class for styling
            .toFactory();

    public String sanitize(String rawHtml) {
        if (rawHtml == null) return null;
        return POLICY.sanitize(rawHtml);
    }

    /**
     * After sanitizing, a value like "<p></p>" or "<p><br></p>" is non-blank
     * as a raw string but has NO actual visible content — e.g. someone
     * submitted only a <script> tag, which sanitizes down to nothing. Used
     * to validate TEXT posts and comments actually contain something a
     * reader would see, not just surviving markup skeleton.
     */
    public boolean hasVisibleText(String sanitizedHtml) {
        if (sanitizedHtml == null) return false;
        return !sanitizedHtml.replaceAll("<[^>]*>", "").trim().isEmpty();
    }
}