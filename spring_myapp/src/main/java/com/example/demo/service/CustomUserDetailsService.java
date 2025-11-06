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

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CallUserRepository callUserRepository;
    
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CallUser user = callUserRepository.findByUserName(username);
        System.out.println("ログインユーザー情報: " + user);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // role を Spring Security 権限名に変換する
        String roleName;
        System.out.println("ユーザーroleカラムの実際の値: " + user.getRole());

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
        } catch (NumberFormatException e) {
            roleName = "USER"; // 念のため
        }
        System.out.println(Collections.singleton(new SimpleGrantedAuthority(roleName)));


        // Spring SecurityのUserDetailsを返す
        return new User(
                user.getUserName(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(roleName))
        );
    }
    
 // 既存ユーザーのハッシュ化
    public void hashExistingPasswords() {
        List<CallUser> users = callUserRepository.findAll();

        for (CallUser user : users) {
            String pw = user.getPassword();
            // すでにハッシュ化されているものはスキップ（BCryptは$2a$または$2b$で始まる）
            if (pw.startsWith("$2a$") || pw.startsWith("$2b$")) {
                System.out.println(user.getUserName() + " はすでにハッシュ化済み");
                continue;
            }

            // ハッシュ化
            String encoded = passwordEncoder.encode(pw);
            user.setPassword(encoded);
            callUserRepository.save(user);
            System.out.println(user.getUserName() + " のパスワードをハッシュ化しました");
        }
    }
    
   // 新規登録用メソッド
    public CallUser registerUser(String username, String rawPassword, int role) {
        CallUser user = new CallUser();
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(String.valueOf(role)); // int → String に変換
        return callUserRepository.save(user);
    }
}
