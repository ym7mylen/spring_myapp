package com.example.demo.model;

public class LogStatusUpdateRequest {
    private Long id;// 通話ログID
    private Integer statusKakunin;// 確認者用ステータス（0=未確認, 1=確認済）

    private Integer statusKanri;// 管理者用ステータス（0=未確認, 1=確認済）

  // 以下は各カラムの値を取得、カラムに値を設定
    public LogStatusUpdateRequest() {}

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

    @Override// 文字列にする時の型を定義
    public String toString() {
        return "LogStatusUpdateRequest{" +
                "id=" + id +
                ", statusKakunin=" + statusKakunin +
                ", statusKanri=" + statusKanri +
                '}';
    }
}
