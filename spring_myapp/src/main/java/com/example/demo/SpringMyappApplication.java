package com.example.demo;

import org.springframework.boot.SpringApplication;//Spring Bootアプリケーションを起動
import org.springframework.boot.autoconfigure.SpringBootApplication;// Spring Bootアプリケーションの自動設定を有効化

@SpringBootApplication
public class SpringMyappApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringMyappApplication.class, args);//Spring Bootアプリケーションを起動
	}

}
