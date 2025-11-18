package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.ItemEntity;
import com.example.demo.repository.ItemRepository;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    // CSV登録用：insert or update
    public void insertOrUpdate(ItemEntity item) {
        if (item.getId() != null && itemRepository.existsById(item.getId())) {
            // 更新
            ItemEntity existing = itemRepository.findById(item.getId()).orElse(null);
            if (existing != null) {
                existing.setName(item.getName());
                existing.setCategory(item.getCategory());
                itemRepository.save(existing);
            }
        } else {
            // 新規登録
            itemRepository.save(item);
        }
    }

    // 画面表示用
    public List<ItemEntity> findAll() {
        return itemRepository.findAll();
    }

    // CallLogに商品を紐付ける用
    public ItemEntity findById(Long id) {
        return itemRepository.findById(id).orElse(null);
    }
}
