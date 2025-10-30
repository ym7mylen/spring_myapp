package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.model.CallLog;
import com.example.demo.repository.CallLogRepository;


@Controller
public class HomeController {

    @Autowired
    private CallLogRepository callLogRepository;

    // トップページ（/）にアクセスした時に、top.htmlを表示
    @GetMapping("/detail")
    public String home(Model model) {
    	 List<CallLog> callLogs = callLogRepository.findAll();

         // 取得したデータをモデルに追加してビューに渡す
         model.addAttribute("callLogs", callLogs);
         
         System.out.println("CallLogs:");
         for (CallLog log : callLogs) {
             System.out.println(log);  // log.toString() が呼ばれる（toStringがオーバーライドされていれば）
         }
    	
         return "detail";  // templates/detail.htmlを返す
    }

}
