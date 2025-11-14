package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {
	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf
                .requireCsrfProtectionMatcher(request -> { // CSRF保護が必要なリクエストの条件を指定
                    return "POST".equals(request.getMethod());
                })
            );
		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/register", "/login", "/success","/style.css/**", "/upload/mp4/**").permitAll()
				.requestMatchers("/updateStatus").hasAnyRole("ADMIN", "CONFIRM")  // ステータス更新は管理者と確認者だけ許可
				.anyRequest().authenticated()
			)
			.formLogin(form -> form
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.defaultSuccessUrl("/", true)// ログイン成功後にTOP画面へ
				.failureUrl("/login?error=true")
				.permitAll()
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
			);
		return http.build();
    	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {// パスワード暗号化
		return new BCryptPasswordEncoder();
	}
}
