package com.example.demo.service;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.ItemEntity;
import com.example.demo.repository.ItemRepository;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private MonthlyCsvBatch monthlyCsvBatch;


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
    
 // 前月のCSVファイルを読み込み、商品リストを取得する
    public List<ItemEntity> getItemsFromLastMonthCsv(String baseDir) {
        // 前月のCSVファイルを取得
        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);

     // LocalDateTimeで始まりと終わりの時間を設定
        LocalDateTime startOfMonth = lastMonth.atStartOfDay();  // 前月の00:00:00
        LocalDateTime endOfMonth = lastMonth.atTime(23, 59, 59);  // 前月の23:59:59

        // 前月のアイテムをDBから取得
        List<ItemEntity> items = itemRepository.findByCreatedAtBetween(startOfMonth, endOfMonth);

        // フォルダ名とCSVファイルの処理を行う
        String folderName = lastMonth.format(DateTimeFormatter.ofPattern("yyyy_MM"));
        File monthlyDir = new File(baseDir + folderName);
        // フォルダが存在する場合に最新のCSVファイルを取得
        if (!monthlyDir.exists()) {
            System.out.println("CSVフォルダが存在しません: " + monthlyDir.getAbsolutePath());
            return null;
        }

        // 最新のCSVファイルを取得
        File[] files = monthlyDir.listFiles((dir, name) -> name.endsWith(".csv"));
        if (files == null || files.length == 0) {
            System.err.println("CSVファイルが存在しません: " + monthlyDir.getAbsolutePath());
            return null;
        }

     // すべてのCSVファイルを読み込んでアイテムを追加
        for (File file : files) {
            List<ItemEntity> itemsFromCsv = monthlyCsvBatch.readCsvFile(file);
            if (itemsFromCsv != null) {
                for (ItemEntity item : itemsFromCsv) {
                    // CSVアイテムにIDを設定
                    ItemEntity dbItem = itemRepository.findByName(item.getName()); // 商品名でIDを取得
                    if (dbItem != null) {
                        item.setId(dbItem.getId());  // DBから取得したIDをセット
                    } else {
                        // IDが存在しない場合は新規に保存してIDを取得
                        item = itemRepository.save(item); // 新規アイテムを保存
                    }
                    items.add(item);  // アイテムリストに追加
                }
            }
        }
        

        return items; 
    }
 
    // 2ヶ月前（前々月）のCSVファイルを読み込み、商品リストを取得する
    public List<ItemEntity> getItemsFromSecondLastMonthCsv(String baseDir) {
        // 2ヶ月前を取得
        LocalDate currentDate = LocalDate.now();
        LocalDate secondLastMonthDate = currentDate.minusMonths(2);
        
        // LocalDateTimeで始まりと終わりの時間を設定
        LocalDateTime startOfMonth = secondLastMonthDate.atStartOfDay();
        LocalDateTime endOfMonth = secondLastMonthDate.atTime(23, 59, 59);

        // 前々月のアイテムをDBから取得
        List<ItemEntity> items = itemRepository.findByCreatedAtBetween(startOfMonth, endOfMonth);

        // フォルダ名とCSVファイルの処理を行う
        String folderName = secondLastMonthDate.format(DateTimeFormatter.ofPattern("yyyy_MM"));
        File monthlyDir = new File(baseDir + folderName);
        // フォルダが存在しない場合はnullを返す
        if (!monthlyDir.exists()) {
            System.out.println("前々月のCSVフォルダが存在しません: " + monthlyDir.getAbsolutePath());
            return null;
        }

        // 最新のCSVファイルを探す
        File[] files = monthlyDir.listFiles((dir, name) -> name.endsWith(".csv"));
        if (files == null || files.length == 0) {
            System.out.println("前々月のCSVファイルが存在しません: " + monthlyDir.getAbsolutePath());
            return null;
        }

     // すべてのCSVファイルを読み込んでアイテムを追加
        for (File file : files) {
            List<ItemEntity> itemsFromCsv = monthlyCsvBatch.readCsvFile(file);
            if (itemsFromCsv != null) {
                for (ItemEntity item : itemsFromCsv) {
                    // CSVアイテムにIDを設定
                    ItemEntity dbItem = itemRepository.findByName(item.getName()); // 商品名でIDを取得
                    if (dbItem != null) {
                        item.setId(dbItem.getId());  // DBから取得したIDをセット
                    } else {
                        // IDが存在しない場合は新規に保存してIDを取得
                        item = itemRepository.save(item); // 新規アイテムを保存
                    }
                    items.add(item);  // アイテムリストに追加
                }
            }
        }
        
        return items;  // 読み込んだアイテムを返す
    }

 // item_idと名前をDBから取得
    public List<ItemEntity> getItemsByIds(List<Long> itemIds) {
        return itemRepository.findAllById(itemIds);  // itemIdsに一致するIDの商品情報をDBから取得
    }
}
