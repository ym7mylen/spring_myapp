package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;//Spring Bootアプリケーションを起動
import org.springframework.boot.autoconfigure.SpringBootApplication;// Spring Bootアプリケーションの自動設定を有効化

import com.example.demo.service.CustomUserDetailsService;

@SpringBootApplication
public class SpringMyappApplication implements CommandLineRunner {

	@Autowired
    private CustomUserDetailsService customUserDetailsService; // ← サービスをDI

	public static void main(String[] args) {
		SpringApplication.run(SpringMyappApplication.class, args);//Spring Bootアプリケーションを起動
	}

	@Override
    public void run(String... args) throws Exception {
        // アプリ起動時に平文パスワードをハッシュ化
        customUserDetailsService.hashExistingPasswords();
    }
}
