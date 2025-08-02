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
import com.example.internalbooks.dto.DtoCheckedOutBook;
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
    	logger.info("★★★★★★★★★★★Loginされました★★★★★★★★★★★");
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
            
            logger.info("★★★ 貸出中書籍ページアクセス: ユーザーID = {} ★★★", userId);
            
            // 現在のユーザーの貸出中書籍を取得
            List<DtoCheckedOutBook> checkedOutBooks = tBookService.getCheckedOutBooksByUserId(userId);
            
            logger.info("★★★ 取得した貸出中書籍数: {} ★★★", checkedOutBooks != null ? checkedOutBooks.size() : 0);
            
            // 書籍リストをModelに設定（全ての書籍を一度に表示）
            model.addAttribute("checkedOutBooks", checkedOutBooks);
            
            if (checkedOutBooks != null && !checkedOutBooks.isEmpty()) {
                logger.info("★★★ 貸出中書籍を縦並びで表示: 書籍数 = {} ★★★", checkedOutBooks.size());
                for (DtoCheckedOutBook book : checkedOutBooks) {
                    logger.info("★★★ 表示書籍: ID = {}, タイトル = {} ★★★", book.getBookId(), book.getTitle());
                }
            } else {
                logger.info("★★★ 貸出中書籍なし: ユーザーID = {} ★★★", userId);
            }

            return "page/checkedout";
    	}
    	catch (Exception e) {
    		logger.error("★★★ 貸出中書籍ページエラー: {} ★★★", e.getMessage());
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
            
            //デバッグ用ログ
            logger.info("QRスキャナーページにアクセスされました");

            return "page/qrsearch";
    	}
    	catch (Exception e) {
    		// 認証失敗時はログインページにリダイレクト
            return error(redirectAttributes);
    	}

    }
    
    /**
     * QRコード読み取り結果表示ページ
     * ★テストのためダミーページにしている★
     */
    @GetMapping("/page/dummy")
    public String dummy(@RequestParam(name = "qrData", required = false) String qrData, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    	try {
    		// JWT認証トークンの検証
    		String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
            // QRデータの取得（パラメータまたはセッションから）
            String finalQrData = qrData;
            if (finalQrData == null || finalQrData.isEmpty()) {
            	// セッションからQRデータを取得（POSTリダイレクト方式の場合）
            	finalQrData = (String) session.getAttribute("qrData");
            	// セッションから取得後は削除
            	session.removeAttribute("qrData");
            }

            // QRコードデータをThymeleafテンプレートに渡す
            model.addAttribute("qrData", finalQrData);

            return "page/dummy";    //テストのため遷移先をdummyにしている
    	}
    	catch (Exception e) {
    		// 認証失敗時はログインページにリダイレクト
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

    /**
     * エラー処理
     * セッション切れなどの際にloginページにリダイレクト
     */
    private String error(RedirectAttributes redirectAttributes) {
    	redirectAttributes.addFlashAttribute("errorMessage", "セッションが切れました。再度ログインしてください。");
        return "redirect:/page/login";
    }

}