package com.example.demo.controller;

import java.util.List;//Listを使う

import org.springframework.beans.factory.annotation.Autowired;//自動的に依存関係を入れる
import org.springframework.stereotype.Controller;//コントローラークラス
import org.springframework.ui.Model;//コントローラーからビューにデータを渡す
import org.springframework.web.bind.annotation.GetMapping;// HTTPのGETリクエストを処理

import com.example.demo.model.CallLog;//モデルクラスCallLogをインポート
import com.example.demo.repository.CallLogRepository;//リポジトリクラスをインポート

@Controller
public class HomeController {

    @Autowired//自動的に依存関係を入れる
    private CallLogRepository callLogRepository;

    //トップページ（/）にアクセスした時に、top.htmlを表示
    @GetMapping("/detail")//「http://localhost:8080/detail」で実行
    public String home(Model model) {//引数の Model は、コントローラーからビューへ値を渡す
    	 List<CallLog> callLogs = callLogRepository.findAll();//call_logs テーブルの全内容を取得して、callLogsリストに格納

         //取得したデータをモデルに追加してビューに渡す
         model.addAttribute("callLogs", callLogs);//Thymeleafテンプレから ${callLogs} として参照可
         
         System.out.println("CallLogs:");//「CallLogs:」を出力
         for (CallLog log : callLogs) {//1件ずつCallLogの内容をコンソールに出力
             System.out.println(log);  
         }
    	
         return "detail"; //templates/detail.htmlを返す
    }

}
