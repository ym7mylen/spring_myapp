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

    //ゲッターとセッター
    public Long getId() {//idカラムの値を取得
        return id;
    }

    public void setId(Long id) {//idカラムに値を設定
        this.id = id;
    }

    public String getUserName() {//usernameカラムの値を取得
        return userName;
    }

    public void setUserName(String userName) {//usernameカラムに値を設定
        this.userName = userName;
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
    
    @Override
    public String toString() {//このエンティティを文字列にするときの型を定義
        return "CallUser{id=" + id + 
               ", userName=" + userName + 
               ", password='" + password + 
               ", role='" + role + "'}";
    }
}
