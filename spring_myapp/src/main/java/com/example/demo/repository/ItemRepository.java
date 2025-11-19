package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    boolean existsById(Long id);
    
 // createdAt が start 以上、end 以下のレコードを取得
    List<ItemEntity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    boolean existsByName(String name);  // nameの重複チェック
}
