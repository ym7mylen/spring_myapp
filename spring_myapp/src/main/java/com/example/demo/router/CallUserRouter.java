package com.example.demo.router;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.CallUser;
import com.example.demo.repository.CallUserRepository;

@RestController
@RequestMapping("/users2") // パスを変更
public class CallUserRouter {

    @Autowired
    private CallUserRepository callUserRepository;

    // ユーザー一覧の取得
    @GetMapping
    public List<CallUser> getAllUsers() {
        return callUserRepository.findAll();
    }

    // ユーザー詳細の取得
    @GetMapping("/{id}")
    public CallUser getUserById(@PathVariable Long id) {
        Optional<CallUser> user = callUserRepository.findById(id);
        return user.orElse(null); // ユーザーが見つからない場合はnullを返す
    }
}

