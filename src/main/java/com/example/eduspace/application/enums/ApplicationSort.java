package com.example.eduspace.application.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
@AllArgsConstructor
public enum ApplicationSort {
    NEWEST(Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))),
    OLDEST(Sort.by(Sort.Order.asc("createdAt"), Sort.Order.asc("id"))),
    RECENTLY_UPDATED(Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id")));

    private final Sort sort;
}