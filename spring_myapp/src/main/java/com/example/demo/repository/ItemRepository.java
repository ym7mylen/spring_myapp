package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    
    ItemEntity findByName(String name);

    ItemEntity findTopByOrderByCreatedAtDesc();

    // 特定の日付に登録された商品を取得
    List<ItemEntity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    boolean existsById(Long id);
    
    boolean existsByName(String name);  // nameの重複チェック
}
