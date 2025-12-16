package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
       
        registry.addResourceHandler("/upload/mp4/**") // このURLにアクセスされた場合指定したローカルフォルダのファイルを返す設定
        	.addResourceLocations("file:///Users/yuki/Downloads/work/spring_myapp/upload/mp4/");
    }
    
}

