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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;//コントローラークラス
import org.springframework.ui.Model;//コントローラーからビューにデータを渡す
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;// HTTPのGETリクエストを処理
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.CallLog;//モデルクラスCallLogをインポート
import com.example.demo.model.CallUser;
import com.example.demo.model.LogStatusUpdateRequest;
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
            Model model,
            Authentication authentication) {	
    	LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);
        List<CallLog> callLogs = callLogRepository.findByCallDateBetween(startDate, endDate);      
        model.addAttribute("callLogs", callLogs);

     // Spring Security から現在ログイン中のユーザー権限を取得
        String userRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("USER"); // 権限が取れなかった場合のデフォルト
        model.addAttribute("userRole", userRole);
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isConfirm = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CONFIRM"));
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)  // ROLE_ADMIN, ROLE_CONFIRMなど
                .reduce((a, b) -> a + ", " + b)
                .orElse("No roles");
        
     // デバッグ出力
//        System.out.println("===================================================");
//        System.out.println("ログイン中の権限 = " + userRole);
//        System.out.println("isAdmin = " + isAdmin);
//        System.out.println("isConfirm = " + isConfirm);
//        System.out.println("isAdmin = " + roles);
//        System.out.println("===================================================");

        System.out.println("===== 通話ログ filePath 確認 =====");
        for (CallLog log : callLogs) {
            System.out.println("ID=" + log.getId() + ", fileName=" + log.getFileName() + ", filePath=" + log.getFilePath());
        }

        return "detail";
    }

// 登録完了ページ
    @GetMapping("/success")
    public String successPage() {
        return "success"; // success.html
    }
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("callUser", new CallUser()); 
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

     // ファイル保存
        String originalFileName = file.getOriginalFilename();
        File dest = new File(uploadDir + originalFileName);
        file.transferTo(dest);
        
     // DBにセット
        callLog.setFileName(originalFileName);
        callLog.setFilePath(String.format("/logs/%d/%02d/%s",
        		callDate.getYear(),
                callDate.getMonthValue(),
                originalFileName));
        callLog.setCreatedAt(LocalDate.now());
        callLog.setStatusKakunin(0);
        callLog.setStatusKanri(0);
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
    public ResponseEntity<?> serveFile(
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable String fileName) throws IOException {
 // MP4ファイルの保存ディレクトリ
    String uploadDir = "/Users/yuki/git/spring_myapp/upload/mp4/";

    // アクセスされたファイルパス
    Path filePath = Paths.get(uploadDir).resolve(fileName);
    File file = filePath.toFile();

    // ダミーファイル（存在する場合のみ使う）
    File dummyFile = new File(uploadDir + "dummy.mp4");
    //  実際の音声ファイルが存在する場合
    if (file.exists() && file.canRead()) {
        Resource resource = new UrlResource(file.toURI());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
    //  ダミーファイルが存在する場合
    if (dummyFile.exists() && dummyFile.canRead()) {
        System.out.println("実ファイルが存在しないため、ダミーファイルを返します: " + dummyFile.getName());
        Resource resource = new UrlResource(dummyFile.toURI());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + dummyFile.getName() + "\"")
                .body(resource);
    }
    // 実ファイルもダミーファイルも存在しない場合
    System.out.println("音声ファイルもダミーファイルも存在しません: " + filePath.toString());
    String message = "(ファイルが存在しません)";
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
            .body(message);
	}
    // ログ詳細画面への遷移（期間指定）
    @PostMapping("/detail")
    public String showDetail(@RequestParam("from") String fromDate,
                             @RequestParam("to") String toDate,
                             RedirectAttributes redirectAttributes) {
        // ここで日付範囲をチェックして /detail に渡す処理を書く
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	
        return "redirect:/detail?from=" + fromDate + "&to=" + toDate;
    }
    
    
    @CrossOrigin(origins = "http://localhost:8080") // リクエスト元を許可
    @PostMapping("/updateStatus")
    @ResponseBody
    public ResponseEntity<String> updateStatus(@RequestBody LogStatusUpdateRequest request) {
        try {
            // リクエストからログIDと新しいステータスを取得
            Long logId = request.getId();
         // ログIDを使ってログをデータベースから取得
            CallLog callLog = callLogRepository.findById(logId).orElseThrow(() -> new RuntimeException("ログが見つかりません"));

            // 確認者ステータスが送られてきた場合のみ更新
            if (request.getStatusKakunin() != null) {
                callLog.setStatusKakunin(request.getStatusKakunin());
                
            }

            // 管理者ステータスが送られてきた場合のみ更新
            if (request.getStatusKanri() != null) {
                callLog.setStatusKanri(request.getStatusKanri());
            }
//            int newStatus = request.getStatus();
//
//            
//            // 現在のステータスを新しいステータスに更新
//            callLog.setStatus(newStatus);

            // 更新をデータベースに保存
            callLogRepository.save(callLog);

            // 更新成功のレスポンスを返す
            return ResponseEntity.ok("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            // 更新失敗のレスポンスを返す
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新失敗");
        }
    }

}
