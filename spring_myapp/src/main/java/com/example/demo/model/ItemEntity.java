package com.example.demo.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "items")
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;
    
    @CreationTimestamp// 現在時刻が自動的にセット
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp//最終更新日時をセット
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 以下は各カラムの値を取得、カラムに値を設定
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public LocalDateTime getCreatedAt() {
    	return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
    	this.createdAt = createdAt; 
    }
    public LocalDateTime getUpdatedAt() {
    	return updatedAt; 
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
    	this.updatedAt = updatedAt; 
    }
    
    // 文字列化した時の型を定義
    @Override
    public String toString() {
        return "ItemEntity{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", category='" + category + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}


