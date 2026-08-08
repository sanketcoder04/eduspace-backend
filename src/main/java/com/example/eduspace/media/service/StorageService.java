package com.example.eduspace.media.service;

import com.example.eduspace.media.constant.MediaFolder;
import org.springframework.web.multipart.MultipartFile;

/**
 * Storage abstraction so the provider (Cloudflare R2 today) can be swapped
 * without touching any calling module (profile, media controller, etc.).
 */
public interface StorageService {

    /**
     * Uploads a file and returns its publicly accessible URL.
     */
    String upload(MultipartFile file, MediaFolder folder);

    /**
     * Deletes a previously uploaded file, given the URL returned by {@link #upload}.
     * No-ops if the URL doesn't belong to this bucket.
     */
    void delete(String fileUrl);
}