package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.CallUser;

public interface CallUserRepository extends JpaRepository<CallUser, Long> {
}
