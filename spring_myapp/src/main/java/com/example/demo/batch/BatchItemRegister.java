package com.example.demo.batch;

import java.util.List;

import com.example.demo.dto.Item;
import com.example.demo.model.ItemEntity;
import com.example.demo.service.ItemService;

public class BatchItemRegister {

    public static void main(String[] args) {

        System.out.println("=== バッチ処理開始 ===");

        // ダミーの商品リスト
        List<Item> items = List.of(
            new Item(null, "iPhone 15", "スマートフォン"),
            new Item(null, "Galaxy S23", "スマートフォン"),
            new Item(null, "iPad Air", "タブレット")
        );

        ItemService itemService = new ItemService();

        for (Item item : items) {
        	ItemEntity itemEntity = convertToEntity(item);
            itemService.insertOrUpdate(itemEntity);
        }

        System.out.println("=== バッチ処理終了 ===");
    }
 // Item -> ItemEntity に変換するメソッド
    private static ItemEntity convertToEntity(Item item) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(item.getId());  // id は null のままでよい
        itemEntity.setName(item.getName());
        itemEntity.setCategory(item.getCategory());
        return itemEntity;
    }
}


