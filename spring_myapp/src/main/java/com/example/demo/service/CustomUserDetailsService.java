package com.example.demo.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;// 権限を表す
import org.springframework.security.core.userdetails.User;// SecurityのUserDetail実装
import org.springframework.security.core.userdetails.UserDetails;// ユーザー情報を表す
import org.springframework.security.core.userdetails.UserDetailsService;// ユーザー情報を取得
import org.springframework.security.core.userdetails.UsernameNotFoundException;// ユーザーが見つからなかったときに投げる例外
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;// パスワードをBCryptでハッシュ化
import org.springframework.security.crypto.password.PasswordEncoder;// パスワードの照合を抽象化
import org.springframework.stereotype.Service;

import com.example.demo.model.CallUser;// ユーザーモデル
import com.example.demo.repository.CallUserRepository;// データベース操作用のリポジトリ

@Service
public class CustomUserDetailsService implements UserDetailsService {// SpringSecurityの認証でユーザー情報を取得するクラスを実装

    @Autowired
    private CallUserRepository callUserRepository;// ユーザーデータベース操作用リポジトリを自動で入れる
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();// パスワードハッシュ化をを生成（BCryptを使用）

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {// SpringSecurityがログイン時に呼び出すメソッド
        CallUser user = callUserRepository.findByUserName(username);// DBからユーザー名検索してCallUserオブジェクトを取得
        System.out.println("ログインユーザー情報: " + user);// デバッグ用にユーザー情報を表示
        if (user == null) {
            throw new UsernameNotFoundException("User not found");// ユーザーが存在しなければ例外を投げる
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
            roleName = "USER"; 
        }
        System.out.println(Collections.singleton(new SimpleGrantedAuthority(roleName)));// デバッグ用に権限名を表示
        return new User(// Spring SecurityのUserDetailsを返す
                user.getUserName(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(roleName))
        );
    }
    
    public void hashExistingPasswords() {// 既存ユーザーのハッシュ化
        List<CallUser> users = callUserRepository.findAll();// DB から全ユーザーを取得
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
    
    public CallUser registerUser(String username, String rawPassword, int role) {// 新規登録用メソッド
        CallUser user = new CallUser();// 新しいユーザーオブジェクトを作成
        user.setUserName(username);// ユーザー名をセット
        user.setPassword(passwordEncoder.encode(rawPassword));// パスワードをハッシュ化してセット
        user.setRole(String.valueOf(role)); // int → String に変換
        return callUserRepository.save(user);// DB に保存して返す
    }
}
