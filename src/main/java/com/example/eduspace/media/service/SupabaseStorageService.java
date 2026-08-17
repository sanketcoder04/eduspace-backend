package com.example.eduspace.media.service;

import com.example.eduspace.config.properties.StorageProperties;
import com.example.eduspace.exception.BadRequestException;
import com.example.eduspace.media.constant.MediaFolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupabaseStorageService implements StorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "application/pdf"
    );

    private final StorageProperties storageProperties;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public String upload(MultipartFile file, MediaFolder folder) {
        validate(file);

        String key = folder.name().toLowerCase() + "/" + UUID.randomUUID()
                + extractExtension(file.getOriginalFilename());

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(objectEndpoint(key)))
                    .header("Authorization", "Bearer " + storageProperties.getServiceRoleKey())
                    .header("apikey", storageProperties.getServiceRoleKey())
                    .header("Content-Type", file.getContentType())
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 300) {
                log.error("Supabase upload failed [{}]: {}", response.statusCode(), response.body());
                throw new BadRequestException("File upload failed. Please try again.");
            }

        } catch (IOException e) {
            log.error("Failed to read upload stream for key {}", key, e);
            throw new BadRequestException("Could not read the uploaded file.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BadRequestException("File upload was interrupted.");
        }

        return publicUrl(key);
    }

    @Override
    public void delete(String fileUrl) {
        String prefix = publicUrl("");
        if (fileUrl == null || !fileUrl.startsWith(prefix)) {
            return;
        }

        String key = fileUrl.substring(prefix.length());

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(objectEndpoint(key)))
                    .header("Authorization", "Bearer " + storageProperties.getServiceRoleKey())
                    .header("apikey", storageProperties.getServiceRoleKey())
                    .DELETE()
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            log.warn("Failed to delete file {} from Supabase Storage", key, e);
        }
    }

    private String objectEndpoint(String key) {
        return storageProperties.getUrl() + "/storage/v1/object/" + storageProperties.getBucket() + "/" + key;
    }

    private String publicUrl(String key) {
        return storageProperties.getUrl() + "/storage/v1/object/public/" + storageProperties.getBucket() + "/" + key;
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("No file was provided.");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Unsupported file type. Allowed: JPEG, PNG, WEBP, PDF.");
        }
        long maxBytes = storageProperties.getMaxFileSizeMb() * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new BadRequestException("File exceeds the " + storageProperties.getMaxFileSizeMb() + "MB limit.");
        }
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) return "";
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }
}