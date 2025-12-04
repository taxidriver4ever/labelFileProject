package org.example.labelprojectjava.config;

import jakarta.annotation.Resource;
import org.example.labelprojectjava.interceptor.LoggedInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private LoggedInterceptor loggedInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggedInterceptor)
                .excludePathPatterns("/login/**")
                .excludePathPatterns("/files/**")
                .excludePathPatterns("/ws-upload/**");
    }
}
