package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//このクラスは設定するクラスと伝える
//WebConfigクラスはWebMvcConfigurerのインターフェースを実装する
@Configuration
public class WebConfig implements WebMvcConfigurer {

	//addResourceHandler("/URLパス/**").addResourceLocations("実際のファイル場所")
	
	//メソッドの上書き
	//addResourceHandlersという関数はResourceHandlerRegistryクラスの型のオブジェクトをregistryという引数としたもので返り値は無い
	//"/upload/mp4/**"このフォルダをURLにアクセスされた場合
	//"file:///Users/yuki/Downloads/work/spring_myapp/upload/mp4/"このファイルパスが実際のファイル場所を返す
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
       
        registry.addResourceHandler("/upload/mp4/**") // このURLにアクセスされた場合指定したローカルフォルダのファイルを返す設定
        	.addResourceLocations("file:///Users/yuki/Downloads/work/spring_myapp/upload/mp4/");
    }
    
}

