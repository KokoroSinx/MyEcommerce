package com.sky.service;

import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String upload(byte[] bytes, String objectName, String contentType);
}
