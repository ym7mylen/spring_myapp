package com.example.demo.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MonthlyCsvBatch {

    private static final String BASE_DIR = "/Users/yuki/git/spring_myapp/upload/csv/";

    @Autowired
    private JdbcTemplate jdbcTemplate; // Spring JDBCでDB操作
    // 毎月1日の3時に実行
    @Scheduled(cron = "0 0 3 1 * *")
    public void execute() {
        System.out.println("=== CSVバッチ処理開始 ===");

        try {
            // ① 年月フォルダ作成
            LocalDate now = LocalDate.now();
            String folderName = now.format(DateTimeFormatter.ofPattern("yyyy_MM"));
            File monthlyDir = new File(BASE_DIR + folderName);

            if (!monthlyDir.exists()) {
                monthlyDir.mkdirs();
                System.out.println("フォルダ作成: " + monthlyDir.getAbsolutePath());
            }

            // ② CSVファイルの受け取り処理（ここはまだ仮）
            // 実際は：
            // 外部サーバーから取得
            // メール添付を取り出す
            // FTPでダウンロード
            // などに対応

            File csv = new File(monthlyDir, "data.csv");

            // ダミーで空ファイルがない場合は作成（本番では不要）
            if (!csv.exists()) {
                csv.createNewFile();
            }

            System.out.println("CSVファイル保存完了: " + csv.getAbsolutePath());

            // ③ CSV読み込み & DB登録
            try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
                String line;
                while ((line = br.readLine()) != null) {
                    // CSVは「name,category」形式だと仮定
                    String[] values = line.split(",");
                    if (values.length >= 2) {
                        String name = values[0].trim();
                        String category = values[1].trim();

                        // DBにINSERT (UNIQUE制約がある場合は重複を避ける)
                        jdbcTemplate.update(
                            "INSERT IGNORE INTO items(name, category) VALUES (?, ?)",
                            name, category
                        );
                    }
                }
            }
//            // ダミーで空ファイルを作成
//            csv.createNewFile();
//
            System.out.println("CSVファイル保存完了: " + csv.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("=== CSVバッチ処理終了 ===");
    }
}


