//package com.example.demo.service;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import com.example.demo.model.ItemEntity;
//import com.example.demo.repository.ItemRepository; 
//
//@Service
//public class MonthlyCsvBatch {
//
//    private static final String BASE_DIR = "/Users/yuki/git/spring_myapp/upload/csv/";
//
//    @Autowired
//    private ItemRepository itemRepository;
//
//    // 毎月1日の3時に実行
//    @Scheduled(cron = "0 0 1 1 * *")
//    public void execute() {
//        System.out.println("=== CSVバッチ処理開始 ===");
//
//     // 最新CSVのアイテムリストを取得
//        List<ItemEntity> latestItems = readLatestCsv();
//        
//        if (latestItems.isEmpty()) {
//            System.out.println("最新CSVが存在しません。処理を終了します。");
//            return;
//        }
//
//     // 未登録のアイテムだけDBに保存
//        for (ItemEntity item : latestItems) {
//            if (item.getName().isEmpty() || item.getCategory().isEmpty()) {
//                System.out.println("無効なCSV行をスキップ: " + item);
//                continue;
//            }
//
//         // DBでのユニーク制約で重複がある場合、挿入されません
//            try {
//                itemRepository.save(item);  // ユニーク制約があるため、重複はDB側で拒否される
//                System.out.println("登録: " + item.getName() + " / " + item.getCategory());
//
//            } catch (Exception e) {
//                // 重複によるエラーを無視する場合は、ここでエラー処理
//                System.out.println(item.getName() + " は既に存在します。スキップします。");
//            }
//        }
//        System.out.println("=== CSVバッチ処理終了 ===");
//    }
//    /**
//     * 最新のCSVファイルを読み込み、ItemEntityリストを返す
//     */
//    public List<ItemEntity> readLatestCsv() {
//        LocalDate now = LocalDate.now();
//        String folderName = now.format(DateTimeFormatter.ofPattern("yyyy_MM"));
//        File monthlyDir = new File(BASE_DIR + folderName);
//
//        if (!monthlyDir.exists() || !monthlyDir.isDirectory()) {
//            System.out.println("CSVフォルダが存在しません: " + monthlyDir.getAbsolutePath());
//            return Collections.emptyList();
//        }
//
//     // 月ごとのCSVファイルをリスト化
//        File[] files = monthlyDir.listFiles((dir, name) -> name.endsWith(".csv"));
//
//        if (files == null || files.length == 0) {
//            System.err.println("CSVファイルが存在しません: " + monthlyDir.getAbsolutePath());
//            return Collections.emptyList();
//        }
//
//     // CSVファイルを更新日時でソートして最新のものを選ぶ
//        File latestCsvFile = Arrays.stream(files)
//                .max((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()))  // 最新のファイルを選択
//                .orElse(null);
//
//        if (latestCsvFile == null) {
//            System.out.println("最新CSVファイルが見つかりません。");
//            return Collections.emptyList();
//        }
//
//        System.out.println("最新のCSVファイル: " + latestCsvFile.getAbsolutePath());
//
//        List<ItemEntity> itemList = new ArrayList<>();
//        try (BufferedReader br = new BufferedReader(new FileReader(latestCsvFile))) {
//            String line;
//            while ((line = br.readLine()) != null) {
////                String[] values = line.split(",");
//            	line = line.replace("\uFEFF", "").trim(); // BOM対応
//                if(line.isEmpty()) continue;
//                
//                String[] values = line.split(",");
//                if (values.length < 2) {
//                    System.out.println("無効なCSV行をスキップ: " + line);
//                    continue;
//                }
//               
//                String name = values[0].trim();
//                String category = values[1].trim();
////                itemList.add(item);
//
//             // 空文字ならスキップ
//                if (name.isEmpty() || category.isEmpty()) {
//                    System.out.println("CSVに不正な行があります: " + Arrays.toString(values));
//                    continue;
//                }
////                if (!itemRepository.existsByName(name)) {
//                ItemEntity item = new ItemEntity();
//                item.setName(name);
//                item.setCategory(category);
//                itemList.add(item);
////                itemRepository.save(item);
////
////                // SAVE → ID は DB が自動採番
////                itemRepository.save(item);
////
////                System.out.println("登録: " + name + " / " + category);
////                } else {
////                	System.out.println(name + " は既に存在します。スキップします。");
////                }
//            }
//        
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return itemList;
//    }
//    
//}
//
//
package com.example.demo.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.model.ItemEntity;
import com.example.demo.repository.ItemRepository; 

@Service
public class MonthlyCsvBatch {

    private static final String BASE_DIR = "/Users/yuki/git/spring_myapp/upload/csv/";

    @Autowired
    private ItemRepository itemRepository;

    // 毎月1日の1時に実行
    @Scheduled(cron = "0 0 1 * * *")
    public void execute() {
        System.out.println("=== CSVバッチ処理開始 ===");

        //  当月分のフォルダを作成する
        LocalDate now = LocalDate.now();
        String currentMonthFolderName = now.format(DateTimeFormatter.ofPattern("yyyy_MM"));
        File currentMonthDir = new File(BASE_DIR + currentMonthFolderName);

        // 当月フォルダが存在しない場合は作成する
        if (!currentMonthDir.exists()) {
            if (currentMonthDir.mkdirs()) {
                System.out.println("当月フォルダが作成されました: " + currentMonthDir.getAbsolutePath());
            } else {
                System.out.println("当月フォルダの作成に失敗しました: " + currentMonthDir.getAbsolutePath());
                return;
            }
        } else {
            System.out.println("既に当月フォルダが存在します: " + currentMonthDir.getAbsolutePath());
        }
        // 前月のフォルダを読み込む
//        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);  // 1ヶ月前を計算
        String folderName = lastMonth.format(DateTimeFormatter.ofPattern("yyyy_MM"));
        File monthlyDir = new File(BASE_DIR + folderName);

        // 前月のフォルダが存在しない場合は処理を終了
        if (!monthlyDir.exists()) {
            System.out.println("CSVフォルダが存在しません: " + monthlyDir.getAbsolutePath());
            return;
        }

        // 前月のCSVファイルを取得
        File[] files = monthlyDir.listFiles((dir, name) -> name.endsWith(".csv"));
        if (files == null || files.length == 0) {
            System.err.println("CSVファイルが存在しません: " + monthlyDir.getAbsolutePath());
            return;
        }

        // 最新のCSVファイルを取得
        File latestCsvFile = Arrays.stream(files)
                .max((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()))
                .orElse(null);

        if (latestCsvFile == null) {
            System.out.println("最新CSVファイルが見つかりません。");
            return;
        }

        System.out.println("前月のCSVファイル: " + latestCsvFile.getAbsolutePath());

        //  CSVファイルを読み込み、DBに挿入
        List<ItemEntity> itemList = readCsvFile(latestCsvFile);

        //  アイテムをDBに挿入（重複はDB側で処理）
        for (ItemEntity item : itemList) {
            try {
                // DB側のユニーク制約で重複は自動的に無視される
                itemRepository.save(item);  // もし重複していたらエラーになる
                System.out.println("登録: " + item.getName() + " / " + item.getCategory());
            } catch (Exception e) {
                // 重複によるエラーをスキップ
                System.out.println(item.getName() + " は既に存在します。スキップします。");
            }
        }

        System.out.println("=== CSVバッチ処理終了 ===");
    }

    /**
     * CSVファイルを読み込み、ItemEntityリストを返す
     */
    public List<ItemEntity> readCsvFile(File csvFile) {
        List<ItemEntity> itemList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim(); // BOM対応
                if (line.isEmpty()) continue;

                String[] values = line.split(",");
                if (values.length < 2) {
                    System.out.println("無効なCSV行をスキップ: " + line);
                    continue;
                }

                String name = values[0].trim();
                String category = values[1].trim();

                // 空文字ならスキップ
                if (name.isEmpty() || category.isEmpty()) {
                    System.out.println("CSVに不正な行があります: " + Arrays.toString(values));
                    continue;
                }

                // ItemEntityを作成してリストに追加
                ItemEntity item = new ItemEntity();
                item.setName(name);
                item.setCategory(category);
                itemList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemList;
    }
 // CSVファイルを読み込んでItemEntityをデータベースに保存する
    public void processCsvFile(String csvFilePath) throws IOException {
        List<ItemEntity> items = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length == 2) {  // CSVが3列で構成されていると仮定
                    ItemEntity item = new ItemEntity();
                    item.setName(columns[0].trim());  // 商品名
                    item.setCategory(columns[1].trim());  // カテゴリー

                    // CSVファイルから読み込んだデータをリストに追加
                    items.add(item);
                }
            }
        }
        
        // リストに追加されたアイテムをDBに保存
        for (ItemEntity item : items) {
            itemRepository.save(item);  // 保存後、IDが自動的に設定される
        }
    }
   
}
