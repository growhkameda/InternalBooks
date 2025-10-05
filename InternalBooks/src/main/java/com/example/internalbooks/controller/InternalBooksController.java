package com.example.internalbooks.controller;


import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.internalbooks.dto.DtoAuthRequest;
import com.example.internalbooks.dto.DtoBookHistory;
import com.example.internalbooks.dto.DtoBookInfo;
import com.example.internalbooks.service.AuthService;
import com.example.internalbooks.service.TBookService;
import com.example.internalbooks.service.TLendingHistoryService;
import com.example.internalbooks.utils.JwtUtil;

import io.micrometer.common.util.StringUtils;

@Controller
public class InternalBooksController {
	
    //ロガー
	private static final Logger logger = LoggerFactory.getLogger(InternalBooksController.class);
    //DI用フィールド
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final TBookService tBookService;
    
    //コンストラクタインジェクション
    public InternalBooksController(JwtUtil jwtUtil, AuthService authService, TBookService tBookService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.tBookService = tBookService;
    }
    


//    @Autowired
//    private JwtUtil jwtUtil;
//    
//    @Autowired
//    private AuthService authService;
//    
//    @Autowired
//	private TBookService tBookService;
//    
    @Autowired
    private TLendingHistoryService lendingHistoryService;

    
    /**
     * トップページとしてloginを設定
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/page/login";
    }
    
    /**
     * ログインページに遷移
     */
    @GetMapping("/page/login")
    public String Login() {  
        return "page/login";
    }

    /**
     * ログイン処理
     */
    @PostMapping("/action/login")
    public String login(@RequestParam(name = "mailAddress") String mailAddress, @RequestParam(name = "password") String password, HttpSession session, RedirectAttributes redirectAttributes) {
    	
    	try {
        	
        	// 想定通りの入力がされている場合
        	if(StringUtils.isNotEmpty(mailAddress) && StringUtils.isNotEmpty(password)) {
        		// 認証情報を専用のDtoに格納
        		DtoAuthRequest authRequest = new DtoAuthRequest();
        		authRequest.setMailAddress(mailAddress);
        		authRequest.setPassword(password);
        		
        		// ログイン処理を実行し成功したらtokenを設定
        		// 認証が失敗するとエラーがなげられるためCatchにひっかかる
        		String token = authService.login(authRequest);
        		
        		logger.info("ログイン成功: メールアドレス = {}", mailAddress);
        		
        		// セッションにtokenを設定
        		session.setAttribute("token", token);
        		
        		return "redirect:/page/top";
        	}
        	else {
        		throw new Exception("ログイン失敗");
        	}
        	
    	}
    	catch (Exception e) {
    		logger.error("ログイン失敗: メールアドレス = {}", mailAddress);
    		return error(redirectAttributes);
    	}

    }

    /**
     * TOPページに遷移 
     */
    @GetMapping("/page/top")
    public String top(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    	try {
    		// トークンの検証
    		validateTokenAndGetUserId(session);
            
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            model.addAttribute("isAdmin", isAdmin);

            return "page/top";
    	}
    	catch (Exception e) {
    		return error(redirectAttributes);
    	}
        
    }
    
    /**
     * カテゴリーリストを表示
     */
    @GetMapping("/page/categories")
    public String categories(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    	try {
    		// トークンの検証
    		validateTokenAndGetUserId(session);
            
            // カテゴリーリストを取得
            List<String> categoryList = tBookService.getAllCategories();
            
            model.addAttribute("categories", categoryList);

            return "page/categories";
    	}
    	catch (Exception e) {
            return error(redirectAttributes);
    	}
        
    }   

    /**
     * 貸出中書籍ページに遷移 
     */
    @GetMapping("/page/checkedout")
    public String checkedout(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    	try {
    		// tokenの検証とユーザーIDの取得
    		Integer userId = validateTokenAndGetUserId(session);
            
            // 現在のユーザーの貸出中書籍を取得
            List<DtoBookInfo> checkedOutBooks = tBookService.getCheckedOutBooksByUserId(userId);
            
            // ====★★★【テスト用】貸し出し書籍なしの状態をテストする場合は以下をコメントアウト★★★ ===/
            //checkedOutBooks = null;               // null                                     // 
            // ================================================================================//
            
            // 書籍リストをModelに設定（全ての書籍を一度に表示）
            model.addAttribute("checkedOutBooks", checkedOutBooks);
            
            // 貸出中書籍ページからの遷移フラグをセッションに設定
            session.setAttribute("fromCheckedOut", true);

            return "page/checkedout";
    	}
    	catch (Exception e) {
    		return error(redirectAttributes);
    	}

    }
    
    /**
     * QRコードサーチページを表示
     */
    @GetMapping("/page/qrsearch")
    public String qrScanner(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    	try {
    		// JWT認証トークンの検証
    		validateTokenAndGetUserId(session);
            
            // QRサーチページからの遷移フラグをセッションに設定
            session.setAttribute("fromQrSearch", true);

            return "page/qrsearch";
    	}
    	catch (Exception e) {
    		// 認証失敗時はログインページにリダイレクト
            return error(redirectAttributes);
    	}

    }
    
    /**
     * 検索結果詳細ページに遷移
     * (session情報詰め込みすぎた…いつかServiceに移行しないといけない(木俣))
     */
    @GetMapping("/page/searchresult")
    public String searchResult(
        @RequestParam(name = "bookId", required = false) Integer bookId,
        @RequestParam(name = "qrData", required = false) String qrData,
        HttpSession session,
        Model model,
        RedirectAttributes redirectAttributes) {
        
        try {
            // JWT認証トークンの検証
            String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
            // QRサーチからの遷移判定フラグを設定
            Boolean fromQrSearch = (Boolean) session.getAttribute("fromQrSearch");
            model.addAttribute("fromQrSearch", fromQrSearch != null ? fromQrSearch : false);
            
            // 貸出中書籍ページからの遷移判定フラグを設定
            Boolean fromCheckedOut = (Boolean) session.getAttribute("fromCheckedOut");
            model.addAttribute("fromCheckedOut", fromCheckedOut != null ? fromCheckedOut : false);
            
            // 書籍一覧から遷移した場合のフラグ設定
            
            
            // 返却ボタン表示判定（QRサーチまたは貸出中書籍ページからの遷移）
            boolean showReturnButton = (fromQrSearch != null && fromQrSearch) || (fromCheckedOut != null && fromCheckedOut);
            model.addAttribute("showReturnButton", showReturnButton);
            
            // 書籍検索処理をServiceで処理
            DtoBookInfo book = tBookService.processBookSearchRequest(bookId, qrData);
            model.addAttribute("book", book);
            model.addAttribute("categories", book.getCategories());
            
            // 書籍履歴を取得

            

            // TODO 実際の書籍履歴取得機能のロジックをここに記述してください。(サービスに記述したものを引っ張ってくる)
            List<DtoBookHistory> dtoBookHistory;
            
            if(bookId == null) {
            	Integer qrId = Integer.parseInt(qrData);
            	dtoBookHistory = lendingHistoryService.getHistoryByBookId(qrId);
            } else {
            	dtoBookHistory = lendingHistoryService.getHistoryByBookId(bookId);
            }
            
            model.addAttribute("bookHistoryList", dtoBookHistory);
            
            
            // セッションから遷移フラグを削除
            session.removeAttribute("fromQrSearch");
            session.removeAttribute("fromCheckedOut");

            return "page/SearchResult";
            
        } catch (Exception e) {
            logger.error("検索結果詳細ページでエラーが発生しました", e);
            return error(redirectAttributes);
        }
    }
    
    
    /**
     * 貸出完了ページに遷移
     */
    @PostMapping("/page/LendingCompleted")
    public String LendingCompleted(
    		@RequestParam("bookId") Integer bookId,
    		@RequestParam(name = "qrData", required = false) String qrData,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
    	
    	try {
    		// torkenの検証
    		String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
            boolean isAdmin = jwtUtil.extractIsAdmin(token);
            model.addAttribute("isAdmin", isAdmin);
            
            // 書籍検索処理をServiceで処理
            DtoBookInfo book = tBookService.processBookSearchRequest(bookId, qrData);
            model.addAttribute("book", book);
            model.addAttribute("categories", book.getCategories());           
            
            // 書籍履歴を取得
            List<DtoBookHistory> dtoBookHistory;
            
            if(bookId == null) {
            	Integer qrId = Integer.parseInt(qrData);
            	dtoBookHistory = lendingHistoryService.getHistoryByBookId(qrId);
            } else {
            	dtoBookHistory = lendingHistoryService.getHistoryByBookId(bookId);
            }
            
            model.addAttribute("bookHistoryList", dtoBookHistory);
            
            DtoBookHistory latestHistory = dtoBookHistory.isEmpty() ? null : dtoBookHistory.get(0);
            model.addAttribute("bookHistory", latestHistory);
            
            if (bookId == null) {
                redirectAttributes.addFlashAttribute("error", "書籍IDが取得できませんでした");
                return "redirect:/page/top";
            }

            return "page/LendingCompleted";
    	}
    	catch (Exception e) {
    		return error(redirectAttributes);
    	}
        
    }
    
    
    /**
     * 返却完了ページに遷移
     */
    @PostMapping("/page/ReturnCompleted")
    public String ReturnCompleted(
    		@RequestParam("bookId") Integer bookId,
    		@RequestParam(name = "qrData", required = false) String qrData,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
    	
    	try {
    		// torkenの検証
    		String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
            boolean isAdmin = jwtUtil.extractIsAdmin(token);
            model.addAttribute("isAdmin", isAdmin);
            
            // 書籍検索処理をServiceで処理
            DtoBookInfo book = tBookService.processBookSearchRequest(bookId, qrData);
            model.addAttribute("book", book);
            model.addAttribute("categories", book.getCategories());           
            
            // 書籍履歴を取得
            List<DtoBookHistory> dtoBookHistory;
            
            if(bookId == null) {
            	Integer qrId = Integer.parseInt(qrData);
            	dtoBookHistory = lendingHistoryService.getHistoryByBookId(qrId);
            } else {
            	dtoBookHistory = lendingHistoryService.getHistoryByBookId(bookId);
            }
            
            model.addAttribute("bookHistoryList", dtoBookHistory);
            
            DtoBookHistory latestHistory = dtoBookHistory.isEmpty() ? null : dtoBookHistory.get(0);
            model.addAttribute("bookHistory", latestHistory);
            
            if (bookId == null) {
                redirectAttributes.addFlashAttribute("error", "書籍IDが取得できませんでした");
                return "redirect:/page/top";
            }

            return "page/ReturnCompleted";
    	}
    	catch (Exception e) {
    		return error(redirectAttributes);
    	}
        
    }
    
   
    /**
     * カテゴリー詳細ページに遷移
     */
    @GetMapping("/page/book_detail")
    public String book_detail(@RequestParam("category") String category,HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    	try {
    		// トークンの検証
    		validateTokenAndGetUserId(session);
            
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            model.addAttribute("isAdmin", isAdmin);
            
            // カテゴリーの値
            model.addAttribute("category", category);
            

            return "page/book_detail";
    	}
    	catch (Exception e) {
    		return error(redirectAttributes);
    	}
        
    }
    
    
    @GetMapping("/page/finishUserEdit")
    public String finishUserEdit(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    	try {
    		// torkenの検証
    		String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
            boolean isAdmin = jwtUtil.extractIsAdmin(token);
            model.addAttribute("isAdmin", isAdmin);

            return "page/finishUserEdit";
    	}
    	catch (Exception e) {
    		return error(redirectAttributes);
    	}
        
    }
    
    @GetMapping("/page/userConfirmation")
    public String userConfirmation(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    	try {
    		// torkenの検証
    		String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
            boolean isAdmin = jwtUtil.extractIsAdmin(token);
            model.addAttribute("isAdmin", isAdmin);

            return "page/userConfirmation";
    	}
    	catch (Exception e) {
    		return error(redirectAttributes);
    	}
        
    }
    
    @GetMapping("/page/userDeleteConfirmation")
    public String userDeleteConfirmation(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    	try {
    		// torkenの検証
    		String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
            boolean isAdmin = jwtUtil.extractIsAdmin(token);
            model.addAttribute("isAdmin", isAdmin);

            return "page/userDeleteConfirmation";
    	}
    	catch (Exception e) {
    		return error(redirectAttributes);
    	}
        
    }
    
    @GetMapping("/page/userEdit")
    public String userEdit(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    	try {
    		// torkenの検証
    		String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
            boolean isAdmin = jwtUtil.extractIsAdmin(token);
            model.addAttribute("isAdmin", isAdmin);

            return "page/userEdit";
    	}
    	catch (Exception e) {
    		return error(redirectAttributes);
    	}
        
    }
    
    @GetMapping("/page/userDeleteComplete")
    public String userDeleteComplete(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    	try {
    		// torkenの検証
    		String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
            boolean isAdmin = jwtUtil.extractIsAdmin(token);
            model.addAttribute("isAdmin", isAdmin);

            return "page/userDeleteComplete";
    	}
    	catch (Exception e) {
    		return error(redirectAttributes);
    	}
        
    }
    

    /**
     * エラー処理
     * セッション切れなどの際にloginページにリダイレクト
     */
    protected String error(RedirectAttributes redirectAttributes) {
    	redirectAttributes.addFlashAttribute("errorMessage", "セッションが切れました。再度ログインしてください。");
        return "redirect:/page/login";
    }
 
    
    /**
     * 管理者権限エラー処理
     * 管理者権限が必要な機能にアクセスした際にloginページにリダイレクト
     */
    protected String adminPermissionError(RedirectAttributes redirectAttributes) {
    	redirectAttributes.addFlashAttribute("errorMessage", "管理者権限が必要です。");
        return "redirect:/page/login";
    }
    
    /**
     * JWT認証とユーザーID取得の共通処理
     */
    protected Integer validateTokenAndGetUserId(HttpSession session) throws Exception {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            throw new Exception("Token not found in session");
        }
        return jwtUtil.extractUserId(token);
    }
    
    /**
     * JWT認証と管理者権限確認の共通処理
     */
    protected boolean validateTokenAndCheckAdmin(HttpSession session) throws Exception {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            throw new Exception("Token not found in session");
        }
        jwtUtil.extractUserId(token); // トークンの有効性確認
        return jwtUtil.extractIsAdmin(token);
    }

}