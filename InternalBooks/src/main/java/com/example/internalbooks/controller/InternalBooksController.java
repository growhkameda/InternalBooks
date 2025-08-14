package com.example.internalbooks.controller;


import java.util.ArrayList;
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
import com.example.internalbooks.dto.DtoBookInfo;
import com.example.internalbooks.dto.BookHistory;
import com.example.internalbooks.service.AuthService;
import com.example.internalbooks.service.TBookService;
import com.example.internalbooks.utils.JwtUtil;

import io.micrometer.common.util.StringUtils;

@Controller
public class InternalBooksController {
	
	private static final Logger logger = LoggerFactory.getLogger(InternalBooksController.class);

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
	private TBookService tBookService;
    
    /**
     * トップページとしてloginを設定
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/page/login";
    }

    @GetMapping("/page/user")
    public String user() {
    	logger.info("★★★★★★★★★★★user() にアクセスされました");
        return "page/user";
    }
    @GetMapping("/page/adminusertop")
    public String usertop() {
    	logger.info("★★★★★★★★★★★usertop() にアクセスされました");
        return "page/adminusertop";
    }
    @GetMapping("/page/bookediting")
    public String bookediting() {
    	logger.info("★★★★★★★★★★★usertop() にアクセスされました");
        return "page/bookediting";
    }
    @GetMapping("/page/UserConfir")
    public String UserConfir() {
    	logger.info("★★★★★★★★★★★usertop() にアクセスされました");
        return "page/UserConfir";
    }
    @GetMapping("/page/UserRegistrationComplete")
    public String UserRegistrationComplete() {
    	logger.info("★★★★★★★★★★★usertop() にアクセスされました");
        return "page/UserRegistrationComplete";
    }

    @GetMapping("/page/BookingConfirmation")
    public String BookingConfirmation() {
    	logger.info("★★★★★★★★★★★usertop() にアクセスされました");
        return "page/BookingConfirmation";
    }
    @GetMapping("/page/BookingRegistrationComplete")
    public String BookingRegistrationComplete() {
    	logger.info("★★★★★★★★★★★usertop() にアクセスされました");
        return "page/BookingRegistrationComplete";
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
    		// torkenの検証
    		String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
            boolean isAdmin = jwtUtil.extractIsAdmin(token);
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
    		// torkenの検証
    		String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
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
    		String token = (String) session.getAttribute("token");
            Integer userId = jwtUtil.extractUserId(token);
            
            // 現在のユーザーの貸出中書籍を取得
            List<DtoBookInfo> checkedOutBooks = tBookService.getCheckedOutBooksByUserId(userId);
            
            // ====★★★【テスト用】貸し出し書籍なしの状態をテストする場合は以下をコメントアウト★★★ ===//                 //                                   //
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
    		String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
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
            
            // 返却ボタン表示判定（QRサーチまたは貸出中書籍ページからの遷移）
            boolean showReturnButton = (fromQrSearch != null && fromQrSearch) || (fromCheckedOut != null && fromCheckedOut);
            model.addAttribute("showReturnButton", showReturnButton);
            
            // 書籍検索処理をServiceで処理
            DtoBookInfo book = tBookService.processBookSearchRequest(bookId, qrData);
            model.addAttribute("book", book);
            
            // 書籍履歴を取得
            List<BookHistory> bookHistory = new ArrayList<>();
            // TODO 実際の書籍履歴取得機能のロジックをここに記述してください。(サービスに記述したものを引っ張ってくる)
            model.addAttribute("bookHistory", bookHistory);
            
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
     * カテゴリー詳細ページに遷移
     */
    @GetMapping("/page/book_detail")
    public String book_detail(@RequestParam("category") String category,HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    	try {
    		// torkenの検証
    		String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
            boolean isAdmin = jwtUtil.extractIsAdmin(token);
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

<<<<<<< HEAD
            return "page/finishUserEdit";
    	}
    	catch (Exception e) {
    		return error(redirectAttributes);
    	}
        
    }
    
    @GetMapping("/page/userConfirmationScreen")
    public String userConfirmationScreen(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
=======
    @GetMapping("/page/finishUserEdit")
    public String finishUserEdit(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
>>>>>>> c989805ab1ababadf35ebb236717c24d4bc53ac1
    	try {
    		// torkenの検証
    		String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
            boolean isAdmin = jwtUtil.extractIsAdmin(token);
            model.addAttribute("isAdmin", isAdmin);

<<<<<<< HEAD
            return "page/userConfirmationScreen";
=======
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
>>>>>>> c989805ab1ababadf35ebb236717c24d4bc53ac1
    	}
    	catch (Exception e) {
    		return error(redirectAttributes);
    	}
        
    }
    
    @GetMapping("/page/userDeleteConfirmation")
<<<<<<< HEAD
    /*
     * トークンの検証と管理者かどうかの確認を行い双方ともtrueの場合、ページ遷移する
     */
    public String userDeleteConfirmation(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    	try {
    		// tokenの検証
=======
    public String userDeleteConfirmation(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    	try {
    		// torkenの検証
>>>>>>> c989805ab1ababadf35ebb236717c24d4bc53ac1
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
<<<<<<< HEAD
    
    // エラーページ
=======

    /**
     * 書籍削除確認ページに遷移
     */
    @GetMapping("/page/BookDeletingConfirmation")
    public String BookDeletingConfirmation(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    	try {
    		// torkenの検証
    		String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
            // 管理者権限の検証
            boolean isAdmin = jwtUtil.extractIsAdmin(token);
            
            // 管理者権限がない場合はログインページにリダイレクト
            if (!isAdmin) {
            	return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);

            return "page/BookDeletingConfirmation";
    	}
    	catch (Exception e) {
    		return error(redirectAttributes);
    	}
        
    }

    /**
     * エラー処理
     * セッション切れなどの際にloginページにリダイレクト
     */
>>>>>>> c989805ab1ababadf35ebb236717c24d4bc53ac1
    private String error(RedirectAttributes redirectAttributes) {
    	redirectAttributes.addFlashAttribute("errorMessage", "セッションが切れました。再度ログインしてください。");
        return "redirect:/page/login";
    }
    
    /**
     * 管理者権限エラー処理
     * 管理者権限が必要な機能にアクセスした際にloginページにリダイレクト
     */
    private String adminPermissionError(RedirectAttributes redirectAttributes) {
    	redirectAttributes.addFlashAttribute("errorMessage", "管理者権限が必要です。");
        return "redirect:/page/login";
    }

}