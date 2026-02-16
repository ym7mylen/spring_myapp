package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.service.CustomUserDetailsService;

//@Configuration: このクラスは設定クラスと知らせる
@Configuration
public class SecurityConfig {
	//依存注入
	//CustomUserDetailsServiceクラスの型のオブジェクトをuserDetailsServiceという変数としこのクラスからアクセスできる
	@Autowired
	private CustomUserDetailsService userDetailsService;

	//spring管理下のオブジェクト
	//securityFilterChainという変数は、HttpSecurityクラス型のオブジェクトのhttpからなりSecurityFilterChain型の返り値を返しエラー時はExceptionを投げる
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		//requireCsrfProtectionMatcher：どのリクエストに対してCSRFチェックを行うかを決めるもの
		
		//リクエストから取得したhttpメソッドとPOSTメソッドが同じかを判定して返す
		http
			.csrf(csrf -> csrf
                .requireCsrfProtectionMatcher(request -> { // CSRF保護が必要なリクエストの条件を指定
                    return "POST".equals(request.getMethod());
                })
            );
		
		//authorizeHttpRequests: URLごとのアクセスルールを決めるところ
		
		//URLごとにアクセスルールをきめ、authという引数を受け取ってauthを使っている
		///register", "/login", "/success","/style.css/**", "/upload/mp4/**のリクエストのURLの時は誰でもログインできる
		//それ以外は認証必須
		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/register", "/login", "/success","/style.css/**", "/upload/mp4/**").permitAll()
				.requestMatchers("/updateStatus").hasAnyRole("ADMIN", "CONFIRM")  // ステータス更新は管理者と確認者だけ許可
				.anyRequest().authenticated()
			)
			
			//フォームでのログインではformという引数を受け取ってformを使う
			//ログインページのURLは/login
			//ログイン成功時のURLは/login
			//ログイン成功後のURLは"/", true
			//誰でもアクセスできる
			.formLogin(form -> form
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.defaultSuccessUrl("/", true)// ログイン成功後にTOP画面へ
				.failureUrl("/login?error=true")
				.permitAll()
			)
			//ログアウトはlogoutという引数を取得しlogoutを使う
			//ログアウトのURLは/logout
			//ログアウト成功時のURLは"/login?logout"
			//httpに返す
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
			);
		return http.build();
    	}
	
	//spring管理下のオブジェクト
	//PasswordEncoderクラスの型のオブジェクトのpasswordEncoderという変数は他のクラスからアクセスできる
	//新しくハッシュ化したパスワードを返す
	@Bean
	public PasswordEncoder passwordEncoder() {// パスワード暗号化
		return new BCryptPasswordEncoder();
	}
}

