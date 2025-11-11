package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.CallLog;
import com.example.demo.model.CallUser;
import com.example.demo.model.LogStatusUpdateRequest;
import com.example.demo.repository.CallLogRepository;
import com.example.demo.repository.CallUserRepository;

@Controller
public class HomeController {

    @Autowired
    private CallLogRepository callLogRepository;// 通話ログ用のデータ操作リポジトリ

    @Autowired
    private CallUserRepository callUserRepository;// ユーザー情報用のデータ操作リポジトリ

    @Autowired
    private PasswordEncoder passwordEncoder;// パスワード暗号化用

    // ===============================
    // 　　　　　　新規登録画面
    // ===============================
    @GetMapping("/register")
    public String showRegisterForm(Model model) {// 新規登録フォーム用に空のCallUserオブジェクトをモデルに設定
        model.addAttribute("callUser", new CallUser());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute CallUser user, RedirectAttributes redirectAttributes) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));// 入力パスワードを暗号化してセット
            callUserRepository.save(user);// データベースに保存
            redirectAttributes.addFlashAttribute("success", "登録が完了しました！");// 登録成功メッセージをリダイレクト先に渡す
            return "redirect:/success";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "登録に失敗しました。");// 登録失敗時メッセージ
            return "redirect:/register";
        }
    }
    
    // ===============================
    // 　　　　　　登録完了画面
    // ===============================
    @GetMapping("/success")
    public String successPage() {
        return "success";
    }

    // ===============================
    // 　　　　　　ログイン画面
    // ===============================
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // ===============================
    // 　　　　　　 top画面
    // ===============================
    @GetMapping("/")
    public String topPage(Model model) {// フォーム用に空のCallLogオブジェクトをセット
        model.addAttribute("callLog", new CallLog());
        return "top";
    }

    // ===============================
    // 　　　　通話ログアップロード
    // ===============================
    @PostMapping("/upload")
    public String uploadCallLog(
            @ModelAttribute CallLog callLog,// フォームからの通話ログ情報
            @RequestParam("file") MultipartFile file,// アップロードファイル
            RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {// ファイル未選択時の表示
            redirectAttributes.addFlashAttribute("error", "ファイルを選択してください");
            return "redirect:/";
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();// 現在ログイン中のユーザー情報を取得
            String username = auth.getName();
            CallUser currentUser = callUserRepository.findByUserName(username);
            LocalDate callDate = callLog.getCallDate();// 通話日付がフォームに無い場合は本日の日付を使用
            if (callDate == null) callDate = LocalDate.now();
            String uploadDir = "/Users/yuki/git/spring_myapp/upload/mp4/";// アップロード先ディレクトリを作成
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) uploadFolder.mkdirs();
            String originalFileName = file.getOriginalFilename();// ファイルを保存
            File dest = new File(uploadDir + originalFileName);
            file.transferTo(dest);
            callLog.setFileName(originalFileName);// DB保存用のパス設定
            callLog.setFilePath(String.format("/logs/%d/%02d/%s",
                    callDate.getYear(),
                    callDate.getMonthValue(),
                    originalFileName));
            callLog.setCreatedAt(LocalDate.now());
            callLog.setStatusKakunin(0);// 確認者ステータス初期化
            callLog.setStatusKanri(0);// 管理者ステータス初期化
            callLog.setUserId(currentUser.getId());// 作成者IDセット
            callLogRepository.save(callLog);// DBに保存
            redirectAttributes.addFlashAttribute("success", "ファイルが正常にアップロードされました");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "ファイル保存に失敗しました");
        }
        return "redirect:/";
    }

    // ===============================
    // 指定表示期間の音声ファイル表示・確認
    // ===============================
    @GetMapping("/logs/{year}/{month}/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<?> serveFile(
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable String fileName) throws IOException {
        String uploadDir = "/Users/yuki/git/spring_myapp/upload/mp4/";// MP4ファイル保存ディレクトリ
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        File file = filePath.toFile();
        File dummyFile = new File(uploadDir + "dummy.mp4");
        if (file.exists() && file.canRead()) {// 実ファイルが存在する場合は返す
            Resource resource = new UrlResource(file.toURI());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(resource);
        }
        if (dummyFile.exists() && dummyFile.canRead()) {// 実ファイルが存在しない場合、ダミーファイルを返却
            System.out.println("実ファイルが存在しないため、ダミーファイルを返します: " + dummyFile.getName());
            Resource resource = new UrlResource(dummyFile.toURI());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + dummyFile.getName() + "\"")
                    .body(resource);
        }
        System.out.println("音声ファイルもダミーファイルも存在しません: " + filePath.toString());// 実ファイルもダミーファイルも存在しない場合

        String message = "(ファイルが存在しません)";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
                .body(message);
    }

    // ===============================
    // 　　　　　　ログ詳細画面
    // ===============================
    @GetMapping("/detail")
    public String detail(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr,
            Model model,
            Authentication authentication) {
        LocalDate startDate = LocalDate.parse(startDateStr); //　文字列日付をLocalDateに変換
        LocalDate endDate = LocalDate.parse(endDateStr);
        List<CallLog> callLogs = callLogRepository.findByCallDateBetween(startDate, endDate);// 指定期間の通話ログをDBから取得
        model.addAttribute("callLogs", callLogs);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        String userRole = authentication.getAuthorities().stream()// ログインユーザーの権限取得
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("USER");
        model.addAttribute("userRole", userRole);
        boolean isAdmin = authentication.getAuthorities().stream()// ログインユーザーが管理者か確認者かを判定
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isConfirm = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CONFIRM"));
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .reduce((a, b) -> a + ", " + b)
                .orElse("No roles");
        
        System.out.println("===== 通話ログ filePath 確認 =====");// デバッグ用：ログID・ファイル名・パスを出力
        for (CallLog log : callLogs) {
            System.out.println("ID=" + log.getId() + ", fileName=" + log.getFileName() + ", filePath=" + log.getFilePath());
        }
        return "detail";
    }

 // ===============================
 // 　　 期間指定でログ詳細画面遷移
 // ===============================
    @PostMapping("/detail")
    public String showDetail(@RequestParam("from") String fromDate,
                             @RequestParam("to") String toDate,
                             RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();// 日付パラメータをGETに変換して/detailにリダイレクト
        return "redirect:/detail?from=" + fromDate + "&to=" + toDate;
    }

    // ===============================
    // 　　　ログ確認・管理ボタン更新
    // ===============================
    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/updateStatus")
    @ResponseBody
    public ResponseEntity<String> updateStatus(@RequestBody LogStatusUpdateRequest request) {// CORS許可(あるドメインから別のドメインのサーバーにアクセスしてデータ取得すること)
        try {
            Long logId = request.getId();
            CallLog callLog = callLogRepository.findById(logId)
                    .orElseThrow(() -> new RuntimeException("ログが見つかりません"));// ステータスが送信されている場合のみ更新
            if (request.getStatusKakunin() != null) {
                callLog.setStatusKakunin(request.getStatusKakunin());
            }
            if (request.getStatusKanri() != null) {
                callLog.setStatusKanri(request.getStatusKanri());
            }
            callLogRepository.save(callLog);// 更新内容をDBに保存
            return ResponseEntity.ok("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新失敗");
        }
    }
}
