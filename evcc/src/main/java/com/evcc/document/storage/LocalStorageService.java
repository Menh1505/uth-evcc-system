package com.evcc.document.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
public class LocalStorageService implements StorageService {

    private final Path rootLocation;
    @Autowired
    public LocalStorageService(@org.springframework.beans.factory.annotation.Value("${storage.root:uploads}") String storageRoot) {
        this.rootLocation = Paths.get(storageRoot).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory: " + this.rootLocation, e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        try {
            Path destination = rootLocation.resolve(file.getOriginalFilename()).normalize();
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return destination.toString();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage(), e);
        }
    }
}
