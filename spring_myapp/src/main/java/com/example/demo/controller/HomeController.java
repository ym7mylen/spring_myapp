package com.example.demo.controller;

import java.io.File;
import java.time.LocalDate;
import java.util.List;//Listを使う

import org.springframework.beans.factory.annotation.Autowired;//自動的に依存関係を入れる
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;//コントローラークラス
import org.springframework.ui.Model;//コントローラーからビューにデータを渡す
import org.springframework.web.bind.annotation.GetMapping;// HTTPのGETリクエストを処理
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.CallLog;//モデルクラスCallLogをインポート
import com.example.demo.model.CallUser;
import com.example.demo.repository.CallLogRepository;//リポジトリクラスをインポート
import com.example.demo.repository.CallUserRepository;

@Controller
public class HomeController {

    @Autowired//自動的に依存関係を入れる
    private CallLogRepository callLogRepository;
    
    @Autowired
    private CallUserRepository callUserRepository;

    //トップページ（/）にアクセスした時に、top.htmlを表示
    @GetMapping("/detail")//「http://localhost:8080/detail」で実行
    public String detail(
        @RequestParam("startDate") String startDateStr,
        @RequestParam("endDate") String endDateStr,
            Model model) {
//引数の Model は、コントローラーからビューへ値を渡す
//    	 List<CallLog> callLogs = callLogRepository.findAll();//call_logs テーブルの全内容を取得して、callLogsリストに格納
//
//       //取得したデータをモデルに追加してビューに渡す
//       model.addAttribute("callLogs", callLogs);//Thymeleafテンプレから ${callLogs} として参照可
//         
//       System.out.println("CallLogs:");//「CallLogs:」を出力
//       for (CallLog log : callLogs) {//1件ずつCallLogの内容をコンソールに出力
//           System.out.println(log);  
//       }
//    	
    	LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        List<CallLog> callLogs = callLogRepository.findByCallDateBetween(startDate, endDate);
         
        model.addAttribute("callLogs", callLogs);

        System.out.println("===== 10月の通話ログ一覧 =====");
        for (CallLog log : callLogs) {
            System.out.println(log);
        }
        
        return "detail"; //templates/detail.htmlを返す
    }
    
// 登録完了ページ
    @GetMapping("/success")
    public String successPage() {
        return "success"; // success.html
    }
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("callUser", new CallUser()); // ← これが必要
        return "register";
    }
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    // 完了メッセージをリダイレクト先に渡す
    public String registerUser(@ModelAttribute CallUser user, RedirectAttributes redirectAttributes) {
    try {
    	user.setPassword(passwordEncoder.encode(user.getPassword()));

        callUserRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "登録が完了しました！");
        return "redirect:/success";
    } catch (Exception e) {
        e.printStackTrace();
        redirectAttributes.addFlashAttribute("error", "登録に失敗しました。");
        return "redirect:/register";
    }
    }
    
 // ログイン画面表示（GET）
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html を返す
    }
    
// // ログイン画面表示
//    @PostMapping("/login")
//    public String loginUser(
//        @RequestParam("username") String username,
//        @RequestParam("password") String password,
//        Model model,
//        RedirectAttributes redirectAttributes) {
//
//    	// ユーザー名で検索
//        CallUser user = callUserRepository.findByUserName(username);
//        if (user != null && user.getPassword().equals(password)) {
//            redirectAttributes.addFlashAttribute("loginUser", user.getUserName());
//            return "redirect:/";
//        } else {
//            model.addAttribute("error", "ユーザー名またはパスワードが間違っています");
//            return "login";
//        }
//        if (user != null) {
//            // パスワード比較
//            if (user.getPassword().equals(password)) {
//                // 認証成功 → TOP画面へリダイレクト
//                redirectAttributes.addFlashAttribute("loginUser", user.getUserName());
//                return "redirect:/";
//            } else {
//                // パスワード不一致
//                model.addAttribute("error", "パスワードが間違っています");
//                return "login";
//            }
//        } else {
//            // ユーザーが存在しない
//            model.addAttribute("error", "ユーザー名が存在しません");
//            return "login";
//        }
//        return "login"; // templates/login.html を返す
//    }

    
 // TOP画面表示（アップロードフォーム含む）
    @GetMapping("/")
    public String topPage(Model model) {
        model.addAttribute("callLog", new CallLog()); // フォーム用
        return "top";
    }

    // 通話ログアップロード処理
    @PostMapping("/upload")
    public String uploadCallLog(
        @ModelAttribute CallLog callLog,
        @RequestParam("file") MultipartFile file,
        RedirectAttributes redirectAttributes) {

    if (file.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "ファイルを選択してください");
        return "redirect:/";
        }

    try {
        // 保存先ディレクトリ（プロジェクト直下）
        String uploadDir = "/Users/yuki/git/spring_myapp/upload/mp4/";
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) uploadFolder.mkdirs();
            
           
     //  保存ファイルパス
        String filePath = uploadDir + file.getOriginalFilename();
        file.transferTo(new File(filePath));

        // CallLog に設定
        callLog.setFileName(file.getOriginalFilename());
//      callLog.setFilePath(filePath);
        callLog.setFilePath("aaaa/bbb.mp4");
        callLog.setCreatedAt(LocalDate.now());
        callLog.setStatus(0);
            
        callLog.setUserId(1L); // ← 常に1番のユーザーに紐付け


        callLogRepository.save(callLog);

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "ファイル保存に失敗しました");
        }

        return "redirect:/";
    }

    // ログ詳細画面への遷移（期間指定）
    @PostMapping("/detail")
    public String showDetail(@RequestParam("from") String fromDate,
                             @RequestParam("to") String toDate,
                             RedirectAttributes redirectAttributes) {
        // ここで日付範囲をチェックして /detail に渡す処理を書く
        return "redirect:/detail?from=" + fromDate + "&to=" + toDate;
    }
}
