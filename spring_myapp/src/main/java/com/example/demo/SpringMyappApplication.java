package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.service.CustomUserDetailsService;

@SpringBootApplication
public class SpringMyappApplication {

	@Autowired
    private CustomUserDetailsService customUserDetailsService; // サービスを入れる
	public static void main(String[] args) {
		SpringApplication.run(SpringMyappApplication.class, args);//　Spring Bootアプリケーションを起動
	}

}
