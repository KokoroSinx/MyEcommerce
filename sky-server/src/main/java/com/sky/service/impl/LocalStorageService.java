package com.sky.service.impl;

import com.sky.properties.LocalStorageProperties;
import com.sky.service.StorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
@Profile("dev")
public class LocalStorageService implements StorageService {
    private final LocalStorageProperties properties;

    public LocalStorageService(LocalStorageProperties properties) {
        this.properties = properties;
    }

    @Override
    public String upload(byte[] bytes, String objectName, String contentType) {
        try {
            Path dir = Paths.get(properties.getBaseDir());
            Files.createDirectories(dir);

            Path target = dir.resolve(objectName);
            Files.write(target, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // return visitable URL (if static resource mapping)
            return properties.getPublicUrlPrefix() + objectName;
        } catch (IOException e) {
            throw new RuntimeException("local upload failed", e);
        }
    }
}
