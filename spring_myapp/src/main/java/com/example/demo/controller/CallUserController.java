package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.CallUser;
import com.example.demo.repository.CallUserRepository;

@Controller
@RestController
@RequestMapping("/users")
public class CallUserController {

    // login.html を表示するメソッド
    @GetMapping("/login")
    public String loginPage() {
        return "login";  // login.html を返す
    }

    // register.html を表示するメソッド
    @GetMapping("/register")
    public String registerPage() {
        return "register";  // register.html を返す
    }

    @Autowired
    private CallUserRepository callUserRepository;

    @GetMapping
    public List<CallUser> getAllUsers() {
        return callUserRepository.findAll();
    }

    @PostMapping
    public CallUser createUser(@RequestBody CallUser user) {
        return callUserRepository.save(user);
    }
    
}
