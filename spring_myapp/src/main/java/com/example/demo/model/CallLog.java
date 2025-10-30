package com.example.demo.model;

import java.time.LocalDate;//日付を扱う

import jakarta.persistence.Entity;//データベースのテーブルと対応するエンティティクラス
import jakarta.persistence.GeneratedValue;//主キーの値を自動生成
import jakarta.persistence.GenerationType;//主キー生成戦略を指定
import jakarta.persistence.Id;//IDがテーブルの主キーであることを指定
import jakarta.persistence.Table;//エンティティをテーブルにマッピング

@Entity//データベース上のテーブルと対応するクラス
@Table(name = "call_logs")//このクラスが "call_logs" というテーブルにマッピング
public class CallLog {
    @Id//主キー
    @GeneratedValue(strategy = GenerationType.IDENTITY)//データベース側で自動採番される
    private Long id;//通話ログのID(主キー)
    private Long userId;//通話を行ったユーザーのID
    private LocalDate callDate;//通話が行われた日付
    private String fileName;//通話録音ファイルの名前
    private String filePath;//通話録音ファイルの保存パス
    private LocalDate createdAt;//登録日時
    private int status;//通話ステータス（0＝未確認、1＝確認済み）
    private LocalDate updatedAt;//最終更新日を保存

    //ゲッターとセッター
    public Long getId() {//idカラムの値を取得
        return id;
    }

    public void setId(Long id) {//idカラムに値を設定
        this.id = id;
    }

    public LocalDate getCallDate() {//callDateカラムの値を取得
        return callDate;
    }

    public void setCallDate(LocalDate callDate) {//callDateカラムに値を設定
        this.callDate = callDate;
    }

    public String getFileName() {//fileNameカラムの値を取得
        return fileName;
    }

    public void setFileName(String fileName) {//fileNameカラムに値を設定
        this.fileName = fileName;
    }

    public String getFilePath() {//filePathカラムの値を取得
        return filePath;
    }

    public void setFilePath(String filePath) {//filePathカラムに値を設定
        this.filePath = filePath;
    }

    public LocalDate getCreatedAt() {//createdAtカラムの値を取得
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {//createdAtカラムに値を設定
        this.createdAt = createdAt;
    }

    public Long getUserId() {//userIdカラムの値を取得
        return userId;
    }

    public void setUserId(Long userId) {//userIdカラムに値を設定
        this.userId = userId;
    }

    public int getStatus() {//statusカラムの値を取得
        return status;
    }

    public void setStatus(int status) {//statusカラムに値を設定
        this.status = status;
    }

    public LocalDate getUpdatedAt() {//updatedAtカラムの値を取得
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {//updatedAtカラムに値を設定
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {//このエンティティを文字列にするときの型を定義
        return "CallLog{id=" + id + 
               ", userId=" + userId + 
               ", callDate='" + callDate + 
               "', fileName='" + fileName + 
               "', filePath='" + filePath + 
               "', createdAt='" + createdAt + 
               "', status=" + status + 
               ", updatedAt='" + updatedAt + "'}";
    }

}
