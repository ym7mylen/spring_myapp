package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;//List型を使う

import org.springframework.data.jpa.repository.JpaRepository;//JpaRepositoryをインポートし基本的なデータベース操作を自動的に利用可

import com.example.demo.model.CallLog;//CallLogクラスをインポート

public interface CallLogRepository extends JpaRepository<CallLog, Long> {//JpaRepositoryで自動的に基本的なDB操作が利用可
	
    List<CallLog> findByUserId(Long userId);//ユーザーIDで通話ログを取得する処理を追加
 // 追加：日付範囲指定で検索（2025年10月分など）
    List<CallLog> findByCallDateBetween(LocalDate startDate, LocalDate endDate);

}
