package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    boolean existsById(Long id);
}
