package com.example.demo.model;

import jakarta.persistence.Entity;//@Entityを使う
import jakarta.persistence.GeneratedValue;//主キーの値を自動生成
import jakarta.persistence.GenerationType;//自動採番の戦略を指定
import jakarta.persistence.Id;//主キーを指定

@Entity//データベースのテーブルと対応付けるエンティティクラス
public class CallUser {
    @Id//テーブルの主キー
    @GeneratedValue(strategy = GenerationType.IDENTITY)//IDを自動採番する設定(MySQLで)
    private Long id;//ID。データベースの主キー
    private String username;//ユーザー名
    private String password;//パスワード
    private String role;//ユーザーの権限

    //ゲッターとセッター
    public Long getId() {//idカラムの値を取得
        return id;
    }

    public void setId(Long id) {//idカラムに値を設定
        this.id = id;
    }

    public String getUsername() {//usernameカラムの値を取得
        return username;
    }

    public void setUsername(String username) {//usernameカラムに値を設定
        this.username = username;
    }

    public String getPassword() {//passwordカラムの値を取得
        return password;
    }

    public void setPassword(String password) {//passwordカラムに値を設定
        this.password = password;
    }

    public String getRole() {//roleカラムの値を取得
        return role;
    }

    public void setRole(String role) {//roleカラムに値を設定
        this.role = role;
    }
}
