package com.example.eduspace.media.controller;

import com.example.eduspace.common.dto.ApiResponse;
import com.example.eduspace.media.constant.MediaFolder;
import com.example.eduspace.media.dto.response.UploadResponse;
import com.example.eduspace.media.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Generic upload endpoint reused by every feature that needs a file
 * (profile avatar/cover/resume/certificates/selfie, and future post images).
 * Callers only ever get back a URL; storage details stay encapsulated here.
 */
@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final StorageService storageService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<UploadResponse>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") MediaFolder folder) {

        String url = storageService.upload(file, folder);

        return ResponseEntity.ok(
                ApiResponse.<UploadResponse>builder()
                        .success(true)
                        .message("File uploaded successfully.")
                        .data(UploadResponse.builder().url(url).build())
                        .build()
        );
    }
}