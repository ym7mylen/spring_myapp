package com.example.demo.model;

public class LogStatusUpdateRequest {
    private Long id;      // 通話ログID
    private Integer statusKakunin;

    // 管理者用ステータス（0=未確認, 1=確認済）
    private Integer statusKanri;


    // デフォルトコンストラクタ（必要に応じて追加）
    public LogStatusUpdateRequest() {}

    // ゲッターとセッター
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatusKakunin() {
        return statusKakunin;
    }

    public void setStatusKakunin(Integer statusKakunin) {
        this.statusKakunin = statusKakunin;
    }

    public Integer getStatusKanri() {
        return statusKanri;
    }

    public void setStatusKanri(Integer statusKanri) {
        this.statusKanri = statusKanri;
    }

    @Override
    public String toString() {
        return "LogStatusUpdateRequest{" +
                "id=" + id +
                ", statusKakunin=" + statusKakunin +
                ", statusKanri=" + statusKanri +
                '}';
    }
}