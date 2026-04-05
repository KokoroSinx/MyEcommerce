package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.service.StorageService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {
    private final StorageService storageService;

    public CommonController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        try {
          String originalFilename = file.getOriginalFilename();
          String extension = originalFilename != null && originalFilename.contains(".")
                  ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1)
                  : "";
          String objectName = UUID.randomUUID().toString() + "." + extension;

          String url = storageService.upload(file.getBytes(), objectName, file.getContentType());
          return Result.success(url);
        } catch (Exception e) {
            log.error("upload failed", e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
