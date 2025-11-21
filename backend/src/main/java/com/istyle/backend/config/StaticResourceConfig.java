package com.istyle.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/images/**")
            .addResourceLocations(
                "file:./uploads/images/",
                "classpath:/static/images/")
            .setCachePeriod(0); // disable caching during development
    }
}
