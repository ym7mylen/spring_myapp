package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
       
        registry.addResourceHandler("/upload/mp4/**") // Mac環境の場合。uploadフォルダを外部リソースとして公開
        	.addResourceLocations("file:///Users/yuki/git/spring_myapp/upload/mp4/");
    }
    
}
