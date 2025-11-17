package com.example.demo.batch;

import java.util.List;

import com.example.demo.dto.Item;
import com.example.demo.repository.ItemDao;

public class BatchItemRegister {

    public static void main(String[] args) {

        System.out.println("=== バッチ処理開始 ===");

        // ダミーの商品リスト
//        List<Item> items = List.of(
//                new Item("iPhone 15", "スマートフォン"),
//                new Item("Galaxy S23", "スマートフォン"),
//                new Item("iPad Air", "タブレット")
//        );

     // バッチではまだDBに入っていないので、id は null で良い
        List<Item> items = List.of(
            new Item(null, "iPhone 15", "スマートフォン"),
            new Item(null, "Galaxy S23", "スマートフォン"),
            new Item(null, "iPad Air", "タブレット")
        );

        ItemDao dao = new ItemDao();

        for (Item item : items) {
            dao.insertOrUpdate(item);
        }

        System.out.println("=== バッチ処理終了 ===");
    }
}
