package com.example.eduspace.media.service;

import com.example.eduspace.media.constant.MediaFolder;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String upload(MultipartFile file, MediaFolder folder);

    void delete(String fileUrl);
}