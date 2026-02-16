package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
	// 商品名に基づいてItemEntityを取得する
    ItemEntity findByName(String name);
//
//    // 作成日時で最新のItemEntityを1件取得する
//    ItemEntity findTopByOrderByCreatedAtDesc();

    // 指定された期間内に作成されたItemEntityのリストを取得する
    List<ItemEntity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // 指定されたidやnameのItemEntityが存在するかどうか
    boolean existsById(Long id);//idの重複チェック
    
    boolean existsByName(String name);// nameの重複チェック
}
