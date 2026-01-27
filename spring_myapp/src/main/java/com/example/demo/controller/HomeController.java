package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
import com.example.demo.model.ItemEntity;
import com.example.demo.model.LogStatusUpdateRequest;
import com.example.demo.repository.CallLogRepository;
import com.example.demo.repository.CallUserRepository;
import com.example.demo.repository.ItemRepository;
import com.example.demo.service.ItemService;
import com.example.demo.service.MonthlyCsvBatch;

//springのコントローラー
@Controller
public class HomeController {

	//自動で依存注入
	//ItemRepositoryクラスの型のオブジェクトをitemRepositoryという変数としこのクラスからアクセス可能
	@Autowired
    private ItemRepository itemRepository;
	//CallLogRepositoryクラスの型のオブジェクトのcallLogRepositoryという変数はこのクラスからアクセス可能
	// 通話ログ用のデータ操作リポジトリ
    @Autowired
    private CallLogRepository callLogRepository;
    //CallUserRepositoryクラスの型のオブジェクトをcallUserRepositoryとしこのクラスからアクセス可能
    // ユーザー情報用のデータ操作リポジトリ
    @Autowired
    private CallUserRepository callUserRepository;
    //PasswordEncoderクラスの型のオブジェクトをpasswordEncoderとしこのクラスからアクセス可能である
    // パスワード暗号化用
    @Autowired
    private PasswordEncoder passwordEncoder;
    //ItemServiceクラスの型のオブジェクトをitemServiceという変数としこのクラスからアクセス可能である
    @Autowired
    private ItemService itemService;
    //MonthlyCsvBatchクラスの型のオブジェクトをmonthlyCsvBatchという変数としこのクラスからアクセス可能である
    @Autowired
    private MonthlyCsvBatch monthlyCsvBatch;

    // ===============================
    // 　　　　　　新規登録画面
    // ===============================
    //"/register"のURLでデータを取得
    //modelからなるshowRegisterFormという変数は他のクラスからアクセスできる
    //"callUser"の値で新しくCallUserを作りモデルの中に追加する
    //registerを返す
    // フォームに入力されたデータをこのオブジェクトに格納する設定
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("callUser", new CallUser());
        return "register";
    }

    //RedirectAttributes: リダイレクト先の画面に値（メッセージなど）を渡すための仕組み
    //PasswordEncoder:パスワードハッシュ化する
    //addFlashAttribute: リダイレクト先の画面に「一度だけ使える値」を渡すためのメソッド
    //printStackTrace: エラーがどこで起きたかや呼び出し経路をコンソールに表示する
    
    //registerUserという関数は、入力画面のuserが自動せセットされ、リダイレクト先にメッセージを渡すために使う
    //"/register"のURLでデータを送信
    @PostMapping("/register")
    public String registerUser(@ModelAttribute CallUser user, RedirectAttributes redirectAttributes) {
    	//ユーザーのパスワードを取得しパスワードをハッシュ化したものをユーザーのパスワードに設定する
    	//userを使ってDBの中に保存する
    	//リダイレクト先の画面に１度だけ"success", "登録が完了しました！"と表示させリダイレクト先に値を渡す
    	//文字列型の"redirect:/success"を返す
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));// 入力パスワードを暗号化してセット
            callUserRepository.save(user);// データベースに保存
            redirectAttributes.addFlashAttribute("success", "登録が完了しました！");// 登録成功メッセージをリダイレクト先に渡す
            return "redirect:/success";
           
            //例外として
            //エラーがどこで起きたかログに表示する
            //"error", "登録に失敗しました。"とリダイレクト先の画面に１度だけ表示され値を渡す
            //"redirect:/register"を返す
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "登録に失敗しました。");// 登録失敗時メッセージ
            return "redirect:/register";
        }
    }
    
    // ===============================
    // 　　　　　　登録完了画面
    // ===============================
    //"/success"のURLからデータを取得
    //successPageという関数は
    //文字列型の"success"を返す
    @GetMapping("/success")
    public String successPage() {
        return "success";
    }

    // ===============================
    // 　　　　　　ログイン画面
    // ===============================
    //"/login"というURLからデータを取得する
    //loginPageという関数は他のクラスからアクセスできる
    //文字列型の"login"を返す
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // ===============================
    // 　　　　　　  TOP画面
    // ===============================
    //isEmpty:空かどうかを判定するメソッド
    
    //"/"というURLからデータを取得する
    //topという関数は、モデルを使い、redirectAttributesはリダイレクト先にメッセージを渡すときに使う
    //"callLog"という値の新しいCallLogの引数を作りモデルに追加する
    @GetMapping("/")
    public String top(Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("callLog", new CallLog());

        //"/Users/yuki/Downloads/work/spring_myapp/upload/csv/"のパスから取得した前月分の月次CSVファイルをlatestItemsという変数の中身がItemEntityのListの箱に入れる
        // 前月のCSVファイルを読み込んで商品リストを取得
        List<ItemEntity> latestItems = itemService.getItemsFromLastMonthCsv("/Users/yuki/Downloads/work/spring_myapp/upload/csv/");
        
        //latestItemsがnullまたは空かどうかを判定する
        //"前月のCSVファイルを読み込めませんでした。"と表示する
        if (latestItems == null || latestItems.isEmpty()) {
            System.out.println("前月のCSVファイルを読み込めませんでした。");
            
            //latestItemsは２ヶ月前のCSVファイルを取得したものである
            // 前々月のCSVファイルを読み込む
            latestItems = itemService.getItemsFromSecondLastMonthCsv("/Users/yuki/Downloads/work/spring_myapp/upload/csv/");
            
            //もしlatestItemsがnullまたは空かどうかを判定する
            //"前々月のCSVファイルも見つかりませんでした。"と表示する
            //"error", "前月および前々月のCSVファイルが見つかりませんでした。"とリダイレクト先に１度表示し値を渡す
            //"redirect:/"を返す
            if (latestItems == null || latestItems.isEmpty()) {
                System.out.println("前々月のCSVファイルも見つかりませんでした。");
                redirectAttributes.addFlashAttribute("error", "前月および前々月のCSVファイルが見つかりませんでした。");
                return "redirect:/";  // トップページにリダイレクト
            }
            //"前々月のCSVファイルを使用します。"と表示させる
            System.out.println("前々月のCSVファイルを使用します。");
        }
        
        //itemIdsは中身がLongのList型の箱であり、ItemEntityから一つずつIdを取得したmapをList化してまとめたものである
        //itemsFromDbという変数は中身がItemEntityのList型の箱であり、itemIdsを使って取得したItemsByIdsをitemServiceに入れたものである
        // 取得したCSVファイルの商品IDから、DBを参照して商品の詳細情報を取得
        List<Long> itemIds = latestItems.stream().map(ItemEntity::getId).collect(Collectors.toList());
        List<ItemEntity> itemsFromDb = itemService.getItemsByIds(itemIds);

        //"items"という値のitemsFromDbという引数をモデルの中に追加する
        model.addAttribute("items", itemsFromDb);  // DBから取得した商品情報を画面に表示
   
        //引数のlatestItemsを表示させる
        System.out.println(latestItems);

        //"items"という値のlatestItemsという引数をモデルに追加する
        //"top"に返す
        model.addAttribute("items", latestItems);
        return "top";
    }

    // ===============================
    // 　　　　通話ログアップロード
    // ===============================
    //"/upload"のURLからデータを送信する
    //uploadCallLogという関数は、
    //CallLogクラス型のオブジェクトのcallLogという変数をモデルに追加し
    //リクエストから"file"からMultipartFileクラス型のオブジェクトのfileという変数に入れる
    //リクエストのパラメーター名itemIdをこの引数に対応させパラメーターがなくてもエラーにならず、長整数型のitemIdという変数を入れる
    //リダイレクト先にメッセージを渡すために使う
    //もしitemIdがnullなら
    //"error", "商品を選択してください"と1度だけリダイレクト先に表示させ値を渡す
    //"redirect:/"を返す
    @PostMapping("/upload")
    public String uploadCallLog(
            @ModelAttribute CallLog callLog,// フォームからの通話ログ情報
            @RequestParam("file") MultipartFile file,// アップロードファイル
            @RequestParam(value = "itemId", required = false) Long itemId,
            RedirectAttributes redirectAttributes) {
    	if (itemId == null) {
            redirectAttributes.addFlashAttribute("error", "商品を選択してください");
            return "redirect:/";
        }
    	System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
    	System.out.println("選択された商品ID: " + itemId);
    	//もしfileの中がからかどうか判定し
    	//"error", "ファイルを選択してください"と１度だけリダイレクト先に表示し値を渡す
    	//"redirect:/"を返す
        if (file.isEmpty()) {// ファイル未選択時の表示
            redirectAttributes.addFlashAttribute("error", "ファイルを選択してください");
            return "redirect:/";
        }

        //SecurityContextHolderクラスの中の取得したテキストの中から認証情報を取得したものをauthという変数とする
        //authの中のNameを取得し文字列型のusernameとする
        //callUserRepositoryの中のusernameと一致するDBのusernameを検索しCallUserクラス型のcurrentUserという変数とする
        //callLogの中からCallDateを取得し月日型のcallDateという変数とする
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();// 現在ログイン中のユーザー情報を取得
            String username = auth.getName();
            CallUser currentUser = callUserRepository.findByUserName(username);
            LocalDate callDate = callLog.getCallDate();// 通話日付がフォームに無い場合は本日の日付を使用
            
            //exists:存在するかどうか判定する
            //transferTo:アップロードされたファイルをサーバー上の指定した場所に保存する
            
            //もしcallDateがnullなら現在の月日をcallDateとする
            //"/Users/yuki/Downloads/work/spring_myapp/upload/mp4/"というパスから文字列型のuploadDirという変数とする
            //uploadDirから新しくFileを作ってFile型のuploadFolderという変数とする
            //もしuploadFolderの中が存在しなかった場合uploadFolderを作る
            //fileの中のオリジナルのファイル名を取得したものを文字列型のoriginalFileNameという変数とする
            //uploadDirとoriginalFileNameから新しいFileを作りファイル型のdestという変数とする
            //destを使ってファイルの中の指定された場所に保存する
            if (callDate == null) callDate = LocalDate.now();
            	String uploadDir = "/Users/yuki/Downloads/work/spring_myapp/upload/mp4/";// アップロード先ディレクトリを作成
            	File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) uploadFolder.mkdirs();
            	String originalFileName = file.getOriginalFilename();// ファイルを保存
            	File dest = new File(uploadDir + originalFileName);
            	file.transferTo(dest);
            
            	//itemRepositoryの中のitemIdと一致するitemIdを検索しitemという変数とする
            	//"Invalid item ID: " + itemIdという例外のメッセージを投げる
            	//itemをcallLogの中に設定する
            	// 商品IDから商品エンティティを取得
            ItemEntity item = itemRepository.findById(itemId)
            	.orElseThrow(() -> new RuntimeException("Invalid item ID: " + itemId));
            callLog.setItem(item);

            //originalFileNameからcallLogの中にFileNameを設定する
            //ファイルパスの形式は"/logs/%d/%02d/%s"このような形で、
            //callDateから取得したYear、MonthValue、originalFileNameをFilePathに設定する
            //現在の月日をcallLogの中のCreatedAtに設定する
            //callLogの中のStatusKakuninを0に設定する
            //callLogの中のStatusKanriを0に設定する
            //currentUserから取得したIdをcallLogの中のUserIdに設定する
            //callLogをDBに保存する
            //"success", "ファイルが正常にアップロードされました"とリダイレクト先に１度表示させ値を渡す
            //"selectedItem"というキーでitemをリダイレクト先に１度表示させ値を渡す
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
            redirectAttributes.addFlashAttribute("selectedItem", item); // 商品情報も追加
            
            //IllegalArgumentException:実行時例外（RuntimeExceptionのサブクラス）でメソッドに不正・不適切な引数が渡されたときにスローされる
            //IOException:チェック例外で入出力（I/O）処理で問題が発生した場合にスローされる例外
            
            //入出力で問題だ発生した際の例外として
            //エラーがどこで起きたかや呼び出し経路をコンソールに出力させる
            //"error", "ファイル保存に失敗しました"とリダイレクト先に１度表示させ値を渡す
            //不正な引数が渡された時にスローされる
            //"error", e.getMessage()とリダイレクト先に１度表示し値を渡す
            //例外として
            //"error", "予期しないエラーが発生しました"とリダイレクト先に１度表示させ値を渡す
            //"redirect:/"を返す
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "ファイル保存に失敗しました");
    	} catch (IllegalArgumentException e) {
    		e.printStackTrace();
    		redirectAttributes.addFlashAttribute("error", e.getMessage());
    	} catch (Exception e) {
    		e.printStackTrace();
    		redirectAttributes.addFlashAttribute("error", "予期しないエラーが発生しました");
    	}
        return "redirect:/";
    }

    // ===============================
    // 指定表示期間の音声ファイル表示・確認
    // ===============================
    //@ResponseBody:メソッドの戻り値をそのままHTTPレスポンスのボディとして返す
    //<?>: 戻り値の型は何でも良い
    //serveFile: ファイルを提供する（配信する）
    //@PathVariable: URLのパスの１部を変数として受け取る
    //resolve:曖昧・抽象的なものを、具体的な実体に変換すること
    //canRead:そのファイルが読み取り可能かどうか判定すること
    //toURI: このファイルは“どこにあるか”を URI で表す(場所を表す)
    //resource: 場所や種類を意識せずに扱えるデータ
    //inline: ブラウザにそのまま表示させること
    //text/plain: ただの文字
    
    //"/logs/{year}/{month}/{fileName:.+}"のURLでデータを取得する
    //HTTPレスポンスボディとして返す
    //ResponseEntityクラスはファイルを提供する
    //整数型のyear、month、文字列型のfileNameを投げ、例外は
    //"/Users/yuki/Downloads/work/spring_myapp/upload/mp4/"のファイルパスの場所が文字列型のuploadDirという変数のアップロード先ディレクトリとする
    //アップロード先ディレクトリをパス化してとファイル名と結合してfilePathという変数とする
    //filePathのファイル化したものをファイル型のfileという変数とする
    //uploadDir + "dummy.mp4"から新しくファイルを作りファイル型のdummyFileという変数のダミーファイルとする
    @GetMapping("/logs/{year}/{month}/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<?> serveFile(
            @PathVariable int year,
            @PathVariable int month,
            @PathVariable String fileName) throws IOException {
        String uploadDir = "/Users/yuki/Downloads/work/spring_myapp/upload/mp4/";// MP4ファイル保存ディレクトリ
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        File file = filePath.toFile();
        File dummyFile = new File(uploadDir + "dummy.mp4");
        
        //ファイルの中が存在するか判定しかつ読み込み可能か判定し
        //ファイルの中をURI化したものを新しく作りそれをresourceという変数とする
        //レスポンスOKを返す
        //HTTPのヘッダーが"video/mp4"を返す
        //ファイルの中のnameをファイル名としブラウザで表示させる
        //ボディはデータを使う
        if (file.exists() && file.canRead()) {// 実ファイルが存在する場合は返す
            Resource resource = new UrlResource(file.toURI());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(resource);
        }
        //ダミーファイルが存在するか判定しかつダミーファイルが読み込めるか判定し
        //"実ファイルが存在しないため、ダミーファイルを返します: " + dummyFile.getName()と表示させる
        //ダミーファイルをURI化したものを新しく作りそれをresourceという変数とする
        //レスポンスOKを返す
        //ヘッダーで"video/mp4"を返す
        //ヘッダーでファイルからnameを取得したものをファイル名とし画面に表示させる
        //ボディはデータを使う
        if (dummyFile.exists() && dummyFile.canRead()) {// 実ファイルが存在しない場合、ダミーファイルを返却
            System.out.println("実ファイルが存在しないため、ダミーファイルを返します: " + dummyFile.getName());
            Resource resource = new UrlResource(dummyFile.toURI());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + dummyFile.getName() + "\"")
                    .body(resource);
        }
        //"音声ファイルもダミーファイルも存在しません: " + filePath.toString()を表示させる
        System.out.println("音声ファイルもダミーファイルも存在しません: " + filePath.toString());// デバック用：実ファイルもダミーファイルも存在しない場合
        //"(ファイルが存在しません)"が文字列型のmessageという変数のメッセージ
        //レスポンスOKを返す
        //ヘッダーでUTF-8のテキストを返す
        //ボディはmessageを使う
        String message = "(ファイルが存在しません)";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
                .body(message);
    }

    // ===============================
    // 　　　　　　ログ詳細画面
    // ===============================
    //startDateStr:開始日（start date）を文字列（String）として保持している変数
    
    //"/detail"のURLでデータを取得する
    //detailの関数は
    //リクエストパラメーターで"startDate"は文字列型のstartDateStrという変数の開始日を送る
    //リクエストパラメーターで"endDate"は文字列型のendDateStrという変数の終了日を送る
    //モデルでデータを保持
    //認証情報をauthenticationという変数とする
    //startDateStrを使って月日型にしたものをstartDateという変数とする
    //endDateStrを使って月日型にしたものをendDateという変数とする
    @GetMapping("/detail")
    public String detail(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr,
            Model model,
            Authentication authentication) {
        LocalDate startDate = LocalDate.parse(startDateStr);//　文字列日付をLocalDateに変換
        LocalDate endDate = LocalDate.parse(endDateStr);
        
        //startDateとendDateから該当の期間のデータを探し出したものを中身がCallLogのリスト型の箱をcallLogsとする
        //"callLogs"というキーでcallLogsという引数をモデルに追加する
        //"startDate"というキーでstartDateを引数としてモデルに追加する
        //"endDate"というキーでendDateという引数としモデルに追加する
        List<CallLog> callLogs = callLogRepository.findByCallDateBetween(startDate, endDate);// 指定期間の通話ログをDBから取得
        model.addAttribute("callLogs", callLogs);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        //authenticationの中から認証情報を１ずつ順に取得したものを文字列型のuserRoleとする
        //取得した認証情報を使ってマップとする
        //最初に検索する
        //他は"ROLE_USER"とする
        //"userRole"というキーでuserRoleの引数をモデルに追加する
        String userRole = authentication.getAuthorities().stream()// ログインユーザーの権限取得
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
        model.addAttribute("userRole", userRole);
        
        //anyMatch:少なくとも１つでも条件を満たす要素があるか
        //Path.resolve():2つのパスを結合して新しいパスを作る
        
        //authenticationの中の認証情報を１つずつ取得しAdminかどうかを判定する
        //認証情報が"ROLE_ADMIN"が１つでもあるか
        //authenticationの中の認証情報を１つずつ取得しConfirmかどうかを判定する
        //認証情報が１つでも"ROLE_CONFIRM"があるか
        //認証情報を1つずつ取り出したものを文字列型のrolesとする
        //取得した認証情報をマップとする
        //aとbの２つのパスを結合して新しいパスを作る
        //それ以外は"No roles"とする
        boolean isAdmin = authentication.getAuthorities().stream()// ログインユーザーが管理者か確認者かを判定
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isConfirm = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CONFIRM"));
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .reduce((a, b) -> a + ", " + b)
                .orElse("No roles");
        
        //CallLogのlogの中からcallLogsを取り出す
        //"detail"を返す
        System.out.println("===== 通話ログ filePath 確認 =====");// デバッグ用：ログID・ファイル名・パスを出力
        for (CallLog log : callLogs) {
            System.out.println("ID=" + log.getId() + ", fileName=" + log.getFileName() + ", filePath=" + log.getFilePath());
        }
        return "detail";
    }

 // ===============================
 // 　　 期間指定でログ詳細画面遷移
 // ===============================
    //"/detail"のURLでデータを送信する
    //showDetailという関数は、リクエストパラメーターの"from"を使って文字列型のfromDateとする
    //リクエストパラメーターで"to"を使って文字列型のtoDateとする
    //リダイレクトする
    //SecurityContextHolderの中のテキストの中から認証情報を取得しauthという変数とする
    //リダイレクト先にいつからいつまでといった文字列を返す
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
    //@CrossOrigin：別ドメイン（オリジン）からのリクエストを受け入れる（例：frontend: http://localhost:3000 → backend: http://localhost:8080）
    
    //"http://localhost:8080"のドメインからリクエストを受け入れる
    //"/updateStatus"のURLでデータを送信する
    //レスポンスボディとして
    //リクエストボディがLogStatusUpdateRequestクラス型のオブジェクトのrequestという引数を中身がStringのResponseEntity型の箱に入れその箱の名前をupdateStatusという変数とする
    //リクエストの中から取得したIdは長整数型のlogIdという変数とする
    //callLogRepositoryの中のIdと一致するlogIdをDBから検索したものをcallLogという変数とする
    //例外なら"ログが見つかりません"というエラーメッセージを新しく作って投げる
    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/updateStatus")
    @ResponseBody
    public ResponseEntity<String> updateStatus(@RequestBody LogStatusUpdateRequest request) {// CORS許可(あるドメインから別のドメインのサーバーにアクセスしてデータ取得すること)
        try {
            Long logId = request.getId();
            CallLog callLog = callLogRepository.findById(logId)
                    .orElseThrow(() -> new RuntimeException("ログが見つかりません"));// ステータスが送信されている場合のみ更新
            
            //もしrequestの中の取得したStatusKakuninがnullでない場合
            //取得したStatusKakuninをcallLogに設定する
            //もしリクエストの中の取得したStatusKakuninが1の場合
            //現在の月日をcallLogの中のKakuninAtに設定する
            //そのほかでもし取得したStatusKakuninが1でかつ取得したKakuninAtがnullの場合
            //callLogの中のCreatedAtを取得しKakuninAtに設定する
            // 確認者ボタン
            if (request.getStatusKakunin() != null) {
                callLog.setStatusKakunin(request.getStatusKakunin());
                if (request.getStatusKakunin() == 1) {
                callLog.setKakuninAt(LocalDate.now()); // 確認日時を記録
                }
            } else if (callLog.getStatusKakunin() == 1 && callLog.getKakuninAt() == null) {
                // すでに押されていて日時がまだ入っていない場合は createdAt をコピー
                callLog.setKakuninAt(callLog.getCreatedAt());
            }

            //もしrequestの中の取得したStatusKanriがnullでない場合
            //requestの中のStatusKanriをcallLogの中のStatusKanriに設定する
            //もしrequestの中の取得したStatusKanriが1の場合
            //現在の月日をcallLogの中のKanriAtに設定する
            //その他でもし、callLogの中の取得したStatusKanriが1でかつ取得したKanriAtがnullの場合
            //callLogの中の取得したCreatedAtをcallLogの中のKanriAtに設定する
            // 管理者ボタン
            if (request.getStatusKanri() != null) {
                callLog.setStatusKanri(request.getStatusKanri());
                if (request.getStatusKanri() == 1) {
                callLog.setKanriAt(LocalDate.now());   // 管理日時を記録
                }
            } else if (callLog.getStatusKanri() == 1 && callLog.getKanriAt() == null) {
                callLog.setKanriAt(callLog.getCreatedAt());
            }
            
            //callLogを使ってDBに保存する
            //レスポンスにok("更新成功")と返す
            callLogRepository.save(callLog);// 更新内容をDBに保存
            return ResponseEntity.ok("更新成功");
            
            //例外として
            //エラーがどこで起きたか呼び出し経路を出力させる
            //レスポンスでステータスが"更新失敗"と返す
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新失敗");
        }
    }
}


