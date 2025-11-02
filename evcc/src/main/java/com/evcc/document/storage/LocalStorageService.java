package com.evcc.document.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
public class LocalStorageService implements StorageService {

    private final String uploadDir = "uploads";

    public LocalStorageService() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        try {
            Path destination = Paths.get(uploadDir, file.getOriginalFilename());
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return destination.toString();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage());
        }
    }
}
