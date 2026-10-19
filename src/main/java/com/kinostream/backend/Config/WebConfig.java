package com.kinostream.backend.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Все запросы на http://localhost:8080/uploads/...
        // Spring будет перенаправлять в папку uploads в корне твоего проекта
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}