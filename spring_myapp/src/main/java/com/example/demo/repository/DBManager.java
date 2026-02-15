package com.example.demo.repository;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBManager {
	
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/sql_practice?useSSL=false&serverTimezone=UTC";
//    private static final String URL = "jdbc:mysql://localhost:3306/sql_practice?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";      // DBユーザー名
    private static final String PASSWORD = "YM1028nature!"; // DBパスワード

    public static Connection getConnection() throws Exception {
        // MySQLドライバのロード
        Class.forName("com.mysql.cj.jdbc.Driver");
        // 接続取得
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}


