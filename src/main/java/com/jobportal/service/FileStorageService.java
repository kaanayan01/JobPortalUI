package com.jobportal.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path rootLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        // Project root + uploads
        this.rootLocation = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    /**
     * @param file multipart file from request
     * @param subFolder e.g. "users", "companies", "resumes"
     * @param prefix e.g. "user_1", "company_10"
     * @return relative path to store in DB, e.g. "users/user_1_1711350990.png"
     */
    public String store(MultipartFile file, String subFolder, String prefix) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String originalName = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename())
        );

        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex != -1) {
            extension = originalName.substring(dotIndex);
        }

        String fileName = prefix + "_" + System.currentTimeMillis() + extension;

        try {
            Path folderPath = rootLocation.resolve(subFolder).normalize();
            Files.createDirectories(folderPath);

            Path targetLocation = folderPath.resolve(fileName).normalize();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            // return relative path from rootLocation
            Path relativePath = rootLocation.relativize(targetLocation);
            return relativePath.toString().replace("\\", "/");
        } catch (IOException e) {
            throw new RuntimeException("Could not store file", e);
        }
    }
}