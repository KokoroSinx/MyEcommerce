package com.sky.config;

import com.sky.properties.LocalStorageProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private final LocalStorageProperties localStorageProperties;

    public StaticResourceConfig(LocalStorageProperties localStorageProperties) {
        this.localStorageProperties = localStorageProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + Paths.get(localStorageProperties.getBaseDir()).toAbsolutePath() + "/";
        registry.addResourceHandler("/static/**")
                .addResourceLocations(location);
    }
}
