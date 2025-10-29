package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.CallLog;

public interface CallLogRepository extends JpaRepository<CallLog, Long> {
	// ユーザーIDで通話ログを取得するメソッドを追加
    List<CallLog> findByUserId(Long userId);
}
