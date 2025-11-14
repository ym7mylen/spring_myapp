package com.example.demo.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.CallUser;
import com.example.demo.repository.CallUserRepository;

//SpringSecurityのログイン認証でユーザー情報を取得するクラスを実装
@Service
public class CustomUserDetailsService implements UserDetailsService {

	// ユーザーデータベース操作用リポジトリを自動で入れる
    @Autowired
    private CallUserRepository callUserRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();// パスワードハッシュ化をを生成（BCryptを使用）

    // SpringSecurityがログイン時に呼び出すメソッド
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CallUser user = callUserRepository.findByUserName(username);// DBからユーザー名検索してCallUserオブジェクトを取得
        System.out.println("ログインユーザー情報: " + user);// デバッグ用にユーザー情報を表示
        if (user == null) {
            throw new UsernameNotFoundException("User not found");// ユーザーが存在しなければそのように表示
        } 
        
        String roleName;// roleを権限名に変換
        System.out.println("ユーザーroleカラムの実際の値: " + user.getRole());// DBに保存されているroleカラムの値を確認（デバッグ用）
        try {
            int roleValue = Integer.parseInt(user.getRole()); // roleがString型なら数値に変換
            switch (roleValue) {
                case 3:
                    roleName = "ROLE_ADMIN"; // 管理者
                    break;
                case 2:
                    roleName = "ROLE_CONFIRM"; // 確認者
                    break;
                default:
                    roleName = "ROLE_USER"; // 一般ユーザー
            }
            
        } catch (NumberFormatException e) {// 変換できなければデフォルト権限を設定
            roleName = "ROLE_USER"; 
        }
        
        System.out.println(Collections.singleton(new SimpleGrantedAuthority(roleName)));// デバッグ用に権限名を表示
        return new User(// Spring SecurityのUserDetailsを返す
                user.getUserName(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(roleName))
        );
    }
    
    // 既存ユーザーのハッシュ化
    public void hashExistingPasswords() {
        List<CallUser> users = callUserRepository.findAll();// DBから全ユーザーを取得
        for (CallUser user : users) {
            String pw = user.getPassword();
            
            if (pw.startsWith("$2a$") || pw.startsWith("$2b$")) {// すでにハッシュ化されているものはスキップ（BCryptは$2a$か$2b$で始まる）
                System.out.println(user.getUserName() + " はすでにハッシュ化済み");
                continue;
            }        
            
            String encoded = passwordEncoder.encode(pw);// ハッシュ化
            user.setPassword(encoded);
            callUserRepository.save(user);// DBに更新
            System.out.println(user.getUserName() + " のパスワードをハッシュ化しました");
        }
    }
    
    // 新規登録用メソッド
    public CallUser registerUser(String username, String rawPassword, int role) {
        CallUser user = new CallUser();// 新しいユーザーオブジェクトを作成
        user.setUserName(username);// ユーザー名をセット
        user.setPassword(passwordEncoder.encode(rawPassword));// パスワードをハッシュ化してセット
        user.setRole(String.valueOf(role)); // int → Stringに変換
        return callUserRepository.save(user);// DBに保存して返す
    }
}

