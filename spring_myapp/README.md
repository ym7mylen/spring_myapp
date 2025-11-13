# spring_myapp

# コールログアプリ
顧客対応の通話ログを管理・再生できるWebアプリです。
ユーザーは通話履歴を確認でき、確認者・管理者はログのステータスを更新できます。

## 機能
- ユーザー登録・ログイン
- 通話ログアップデート
- 通話ログ一覧表示
- 音声ファイル再生
- 管理者・確認者によるステータス更新

## システム構成図
[ユーザー]  
   ↓（ブラウザアクセス）  
[Bootstrap CDN]（外部スタイル・JSライブラリ）  
   ↓  
[Webアプリ（Spring Boot + Thymeleaf）]  
   ↓  
[DB（MySQL）]

## 開発環境
- Java 17
- Maven 3.8
- MariaDB 10.4 

## テスト用アカウント
- 一般ユーザー 　ユーザー名: ippan   / パスワード: password
- 確認者 　ユーザー名: kakunin / パスワード: password
- 管理者 　ユーザー名: kanri   / パスワード: password

## 起動手順
1. リポジトリをクローン
2. `application.properties` を設定
3. `./gradlew spring-boot:run` でアプリ起動
4. 起動後、ブラウザで以下のURLにアクセス  
- 新規登録画面  [http://localhost:8080/register](http://localhost:8080/register)
- ログイン画面  [http://localhost:8080/login](http://localhost:8080/login)
- 登録完了画面  [http://localhost:8080/success](http://localhost:8080/success)

## ディレクトリ構成
```
spring_myapp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├──config
│   │   │            │   ├──SecurityConfig.java　　          // セキュリティー設定
│   │   │            │   ├──WebConfig.java　　               //外部フォルダをWebからアクセス可能にする設定
│   │   │           ├── controller
│   │   │           │   ├── HomeController.java             // 画面遷移・ルーティング
│   │   │           ├── model/
│   │   │           │   ├── CallUser.java                   // ユーザーモデル
│   │   │           │   ├── CallLog.java                    // 通話ログモデル
│   │   │           │   ├── LongStatusUpdateRequest.java    //通話ログのステータス更新リクエストを受け取るためのデータクラス」
│   │   │          ├── repository/
│   │   │               ├── CallUserRepository.java         // ユーザー情報のDBアクセス
│   │   │               ├── CallLogRepository.java          // 通話ログのDBアクセス
│   │   │           ├──service
│   │   │           │   ├── CustomUserDetailsService.java   //ユーザー認証サービス
│   │   │           │   ├── LogStatusService.java           //ステータス更新処理
│   │   │          └──SpringMyappApplication.java
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   ├── static/
|   |   |        ├──style.css            //スタイル装飾
│   │   │   ├── templates/
│   │   │   │   ├──fragments
│   │   │               ├──header.html   //共通ヘッダー
│   │   │   │   ├── login.html           //ログイン画面
│   │   │   │   ├── register.html        //新規登録画面
│   │   │   │   ├── top.html             //TOP画面
│   │   │   │   ├── detail.html          //ログ詳細画面
├──upload
               ├──mp4                    //通話ログファイルの保存場所　```
