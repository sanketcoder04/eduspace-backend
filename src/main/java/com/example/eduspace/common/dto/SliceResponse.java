package com.example.eduspace.common.dto;

import java.util.List;

/**
 * Reusable envelope for "load more" lists. Deliberately not Spring's own
 * SliceImpl: serializing Spring Data page/slice implementations directly is
 * discouraged (their JSON shape isn't a stable contract). It carries no total
 * count, so the database never runs a count query.
 */
public record SliceResponse<T>(List<T> content, int page, int size, boolean hasNext) {

    public static <T> SliceResponse<T> empty(int page, int size) {
        return new SliceResponse<>(List.of(), page, size, false);
    }
}