package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity// データベースのテーブルと対応付ける
@Table(name = "call_users")
public class CallUser {
    @Id// テーブルの主キー
    @GeneratedValue(strategy = GenerationType.IDENTITY)// IDを自動採番する設定(MySQLで)
    private Long id;// ID。データベースの主キー
    @Column(name = "username")
    private String userName;// ユーザー名
    private String password;// パスワード
    private String role;// ユーザーの権限

  // 以下は各カラムの値を取得、カラムに値を設定
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    @Override// 文字列にするときの型を定義
    public String toString() {
        return "CallUser{id=" + id + 
               ", userName=" + userName + 
               ", password='" + password + 
               ", role='" + role + "'}";
    }
}


