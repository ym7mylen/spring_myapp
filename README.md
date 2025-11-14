# spring_myapp

# コールログアプリ

顧客対応の通話ログを管理・再生できる Web アプリです。  
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
[Bootstrap CDN]（外部スタイル・JS ライブラリ）  
 ↓  
[Web アプリ（Spring Boot + Thymeleaf）]  
 ↓  
[DB（MySQL）]

## 開発環境

- Java 17
- Maven 3.8
- MariaDB 10.4

## テスト用アカウント

| 役割         | ユーザー名 | パスワード |
| ------------ | ---------- | ---------- |
| 一般ユーザー | ippan      | password   |
| 確認者       | kakunin    | password   |
| 管理者       | kanri      | password   |

## 起動手順

1. リポジトリをクローン
2. `application.properties` を設定
3. `./gradlew spring-boot:run` でアプリ起動
4. 起動後、ブラウザで以下の URL にアクセス

- 新規登録画面 [http://localhost:8080/register](http://localhost:8080/register)
- ログイン画面 [http://localhost:8080/login](http://localhost:8080/login)
- 登録完了画面 [http://localhost:8080/success](http://localhost:8080/success)

## 伝達事項

10/31 と 11/7 に作成された音声ファイルだけ実際の音声（MP4）です。  
それ以外の日付の「音声ファイル」は文字列で空ファイルです。  
実際の音声をアップロードする場合は、拡張子 .mp4 のファイルのみアップロード可能です。

## 頑張ったところ

確認者や管理者といった権限を持つユーザーだけが、詳細情報の閲覧やステータス更新（確認ボタンの操作）が  
できるように、権限管理と機能制御を正しく紐付けた点を特に頑張りました。  
そのために以下のような作業を行いました。  
・Spring Security で適切な権限制御を設定  
・DB では更新可否を表す値が整数と文字列で混在していたため、データ型を統一し整合性を改善  
・権限（ROLE）とステータス更新処理を正しく連動させるように修正  
一つ一つ問題に向き合い、試行錯誤しながら解決していった点が大変でしたが、その分理解も深まりました。

## 工夫した点

Bootstrap を使って基本的なスタイルを整えつつ、  
平面的なボタンや横に広がって見づらいレイアウトを、自作 CSS によって改善し、UI/UX を最適化 しました。

## ディレクトリ構成

```
spring_myapp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├──config
│   │   │           │   ├── SecurityConfig.java　　          // セキュリティー設定
│   │   │           │   └── WebConfig.java　　               // 外部フォルダをWebからアクセス可能にする設定
│   │   │           ├── controller
│   │   │           │   └── HomeController.java             // 画面遷移・ルーティング
│   │   │           ├── model/
│   │   │           │   ├── CallUser.java                   // ユーザーモデル
│   │   │           │   ├── CallLog.java                    // 通話ログモデル
│   │   │           │   └── LongStatusUpdateRequest.java    // 通話ログのステータス更新リクエストを受け取るためのデータクラス
│   │   │           ├── repository/
│   │   │           │   ├── CallUserRepository.java         // ユーザー情報のDBアクセス
│   │   │           │   └── CallLogRepository.java          // 通話ログのDBアクセス
│   │   │           ├──service
│   │   │           │   ├── CustomUserDetailsService.java   // ユーザー認証サービス
│   │   │           │   └── LogStatusService.java           // ステータス更新処理
│   │   │           └── SpringMyappApplication.java
│   │   ├── resources/
│   │   │   ├── templates/
│   │   │   │   └── fragments
│   │   │   │       └── header.html      // 共通ヘッダー
│   │   │   │   ├── login.html           // ログイン画面
│   │   │   │   ├── register.html        // 新規登録画面
│   │   │   │   ├── top.html             // TOP画面
│   │   │   │   └── detail.html          // ログ詳細画面
│   │   │   ├── static/
|   |   |   │   ├── js
│   │   │   │   │   └── detail.js        //詳細画面専用の JavaScript（再生＆ステータス更新処理）
|   |   |   │   └── style.css            // スタイル装飾
│   │   │   └── application.properties
├──upload
    └── mp4                    // 通話ログファイルの保存場所　
```
