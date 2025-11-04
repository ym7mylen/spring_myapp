package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;//Listを使う

import org.springframework.beans.factory.annotation.Autowired;//自動的に依存関係を入れる
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;//コントローラークラス
import org.springframework.ui.Model;//コントローラーからビューにデータを渡す
import org.springframework.web.bind.annotation.GetMapping;// HTTPのGETリクエストを処理
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
    	LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);
        List<CallLog> callLogs = callLogRepository.findByCallDateBetween(startDate, endDate);      
        model.addAttribute("callLogs", callLogs);
//        System.out.println("===== 10月の通話ログ一覧 =====");
//        for (CallLog log : callLogs) {
//            System.out.println(log);
//        }
     // filePath を確認
        System.out.println("===== 通話ログ filePath 確認 =====");
        for (CallLog log : callLogs) {
            System.out.println("ID=" + log.getId() + ", fileName=" + log.getFileName() + ", filePath=" + log.getFilePath());
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
    	// 現在ログインしているユーザー取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // ユーザー名
        CallUser currentUser = callUserRepository.findByUserName(username);

     // callDate を取得（フォームから入る場合）
        LocalDate callDate = callLog.getCallDate();
        if (callDate == null) callDate = LocalDate.now(); // なければ今日

        // 保存先ディレクトリ（プロジェクト直下）
        String uploadDir = "/Users/yuki/git/spring_myapp/upload/mp4/";
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) uploadFolder.mkdirs();       
//           
//     //  保存ファイルパス
//        String filePath = uploadDir + file.getOriginalFilename();
//        file.transferTo(new File(filePath));

     // ファイル保存
        String originalFileName = file.getOriginalFilename();
        File dest = new File(uploadDir + originalFileName);
        file.transferTo(dest);
        // WEBパスを生成：/logs/YYYY/MM/filename
//        String webPath = String.format("/logs/%d/%02d/%s",
//                callDate.getYear(),
//                callDate.getMonthValue(),
//                originalFileName);
//        // CallLog に設定
//        callLog.setFileName(file.getOriginalFilename());     
//        callLog.setFilePath("/upload/mp4/" + file.getOriginalFilename());      
//        callLog.setCreatedAt(LocalDate.now());
//        callLog.setStatus(0);
//        callLog.setUserId(currentUser.getId()); // 現在ユーザーに紐付け
//        System.out.println("保存される filePath = " + callLog.getFilePath());
     // DBにセット
        callLog.setFileName(originalFileName);
        callLog.setFilePath(String.format("/logs/%d/%02d/%s",
        		callDate.getYear(),
                callDate.getMonthValue(),
                originalFileName));
        callLog.setCreatedAt(LocalDate.now());
        callLog.setStatus(0);
        callLog.setUserId(currentUser.getId());
        callLogRepository.save(callLog);
        redirectAttributes.addFlashAttribute("success", "ファイルが正常にアップロードされました");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "ファイル保存に失敗しました");
        }
        return "redirect:/";
    }
    @GetMapping("/logs/{year}/{month}/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable String fileName) throws IOException {
        Path file = Paths.get("/Users/yuki/git/spring_myapp/upload/mp4/").resolve(fileName);
        Resource resource = new UrlResource(file.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
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
