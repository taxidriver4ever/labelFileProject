package org.example.labelprojectjava.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String localPath = "D:/private_project/file-to-label/";

        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + localPath);
    }
}