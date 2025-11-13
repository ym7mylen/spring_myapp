package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.CallLog;

public interface CallLogRepository extends JpaRepository<CallLog, Long> {//JpaRepositoryで自動的に基本的なDB操作が利用可
	
    List<CallLog> findByUserId(Long userId);//ユーザーIDで通話ログを取得する処理を追加
    List<CallLog> findByCallDateBetween(LocalDate startDate, LocalDate endDate); // 追加：日付範囲指定で検索

}
