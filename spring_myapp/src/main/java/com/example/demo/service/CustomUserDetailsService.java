package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
        System.out.println(user);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        System.out.println(User.builder().username(user.getUserName()).password(user.getPassword()).roles("USER").build());
        return User.builder()
                .username(user.getUserName())
                .password(user.getPassword()) // DBに保存されているBCrypt済みパスワード
                .roles("USER") // 必要に応じて変更
                .build();
    }
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
}
