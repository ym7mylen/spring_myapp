package com.example.demo.service;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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
    public List<ItemEntity> getThisMonthItems() {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime start = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime end = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        return itemRepository.findByCreatedAtBetween(start, end);
    }
    
 // 前月のCSVファイルを読み込み、商品リストを取得する
    public List<ItemEntity> getItemsFromLastMonthCsv(String baseDir) {
        // 前月のCSVファイルを取得
        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);
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

        File latestCsvFile = Arrays.stream(files)
                .max((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()))
                .orElse(null);

        if (latestCsvFile == null) {
            System.out.println("最新CSVファイルが見つかりません。");
            return null;
        }

        System.out.println("前月のCSVファイル: " + latestCsvFile.getAbsolutePath());

        // CSVファイルを読み込んで商品リストを返す
        return monthlyCsvBatch.readCsvFile(latestCsvFile);
    }
    // 2ヶ月前（前々月）のCSVファイルを読み込み、商品リストを取得する
    public List<ItemEntity> getItemsFromSecondLastMonthCsv(String baseDir) {
        // 2ヶ月前を取得
        LocalDate currentDate = LocalDate.now();
        LocalDate secondLastMonthDate = currentDate.minusMonths(2);
        
        // 2ヶ月前のフォルダ名を作成（例: "2023_09"）
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

        // 最新のCSVファイルを選択
        File latestCsvFile = Arrays.stream(files)
                .max((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()))
                .orElse(null);

        // CSVファイルを読み込んで商品リストを返す（CSVファイルの読み込みは別メソッドで行う）
        if (latestCsvFile != null) {
            System.out.println("前々月のCSVファイル: " + latestCsvFile.getAbsolutePath());
            return monthlyCsvBatch.readCsvFile(latestCsvFile);
        }
        
        return null; // CSVファイルが見つからなかった場合
    }

}
