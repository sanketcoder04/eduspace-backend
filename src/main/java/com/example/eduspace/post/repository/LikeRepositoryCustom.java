package com.example.eduspace.post.repository;

import com.example.eduspace.post.entity.Like;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface LikeRepositoryCustom {

    Slice<Like> searchPostLikersByName(String postId, String namePart, Pageable pageable);
}