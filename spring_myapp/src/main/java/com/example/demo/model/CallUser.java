package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;//@Entityを使う
import jakarta.persistence.GeneratedValue;//主キーの値を自動生成
import jakarta.persistence.GenerationType;//自動採番の戦略を指定
import jakarta.persistence.Id;//主キーを指定
import jakarta.persistence.Table;


@Entity//データベースのテーブルと対応付けるエンティティクラス
@Table(name = "call_users")
public class CallUser {
    @Id//テーブルの主キー
    @GeneratedValue(strategy = GenerationType.IDENTITY)//IDを自動採番する設定(MySQLで)
    private Long id;//ID。データベースの主キー
    @Column(name = "username")
    private String userName;//ユーザー名
    private String password;//パスワード
    private String role;//ユーザーの権限

    public Long getId() {//以下は各カラムの値を取得、カラムに値を設定
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
    
    @Override
    public String toString() {//文字列にするときの型を定義
        return "CallUser{id=" + id + 
               ", userName=" + userName + 
               ", password='" + password + 
               ", role='" + role + "'}";
    }
}
