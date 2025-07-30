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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.internalbooks.dto.DtoAuthRequest;
import com.example.internalbooks.service.AuthService;
import com.example.internalbooks.service.TBookService;
import com.example.internalbooks.utils.JwtUtil;
import java.io.IOException;
import java.util.Base64;

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
    
    //トップページとしてloginを設定
    @GetMapping("/")
    public String index() {
        return "redirect:/page/login";
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
    
    //ログインページ
    @GetMapping("/page/login")
    public String Login() {
    	logger.info("★★★★★★★★★★★Loginされました★★★★★★★★★★★");
        return "page/login";
    }
    
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
     * QRコードサーチページを表示
     * 
     * @param session HTTPセッション（JWT認証トークン取得用）
     * @param model モデル
     * @param redirectAttributes 認証失敗時のリダイレクト用
     * @return QRスキャナーページのテンプレート名(page/qrsearch) または 認証エラー時のリダイレクト
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
     * テストのためダミーページにしている
     * 
     * @param qrData QRコードから読み取ったデータ（URLパラメータ）
     * @param session HTTPセッション（JWT認証トークン・セッションデータ取得用）
     * @param model Thymeleafテンプレートに渡すモデル
     * @param redirectAttributes 認証失敗時のリダイレクト用
     * @return ダミーページのテンプレート名(page/dummy) または 認証エラー時のリダイレクト
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

            return "page/dummy";    //テストのためdummyにしている
    	}
    	catch (Exception e) {
    		// 認証失敗時はログインページにリダイレクト
            return error(redirectAttributes);
    	}
    }
   
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

    // エラーページ
    private String error(RedirectAttributes redirectAttributes) {
    	redirectAttributes.addFlashAttribute("errorMessage", "セッションが切れました。再度ログインしてください。");
        return "redirect:/page/login";
    }
 // 管理者登録画面の表示
    @GetMapping("/page/adminusertop")
    public String adminusertop() {
        return "page/adminusertop"; // registration.html のパス
    }
    
 // ユーザー登録画面の表示
    @GetMapping("/page/UserRegistration")
    public String UserRegistration() {
        return "page/UserRegistration"; // registration.html のパス
    }

 // ユーザー登録フォームの送信処理
    @PostMapping("/action/UserConfir")
    public String UserConfir(
    	    @RequestParam("employeeId") String employeeId,
    	    @RequestParam("mailAddress") String mailAddress,
    	    @RequestParam("userName") String userName,
    	    @RequestParam("department") String department,
    	    Model model) {

    	    model.addAttribute("employeeId", employeeId);
    	    model.addAttribute("mailAddress", mailAddress);
    	    model.addAttribute("userName", userName);
    	    model.addAttribute("department", department);
    	    
        return "page/UserConfir"; // 同じページに戻り、結果表示
    }
    
    @PostMapping("/action/UserRegistrationComplete")
    public String UserRegistrationComplete(
    	    @RequestParam("employeeId") String employeeId,
    	    @RequestParam("mailAddress") String mailAddress,
    	    @RequestParam("userName") String userName,
    	    @RequestParam("department") String department,
    	    Model model) {

    	    model.addAttribute("employeeId", employeeId);
    	    model.addAttribute("mailAddress", mailAddress);
    	    model.addAttribute("userName", userName);
    	    model.addAttribute("department", department);
    	    
        return "page/UserRegistrationComplete"; // 同じページに戻り、結果表示
    }
    @GetMapping("/page/bookediting")
    public String bookediting() {
        return "page/bookediting"; // registration.html のパス
    }
 // 書籍登録フォームの送信処理
    @PostMapping("/action/BookingConfirmation")
    public String BookingConfirmation(
    	    @RequestParam("bookname") String bookname,
    	    @RequestParam("category") String category,
    	    @RequestParam("offer") String offer,
    	    @RequestParam("comment") String comment,
    	    @RequestParam("imagefile") MultipartFile file,
    	    Model model) {

    	    model.addAttribute("bookname", bookname);
    	    model.addAttribute("category", category);
    	    model.addAttribute("offer", offer);
    	    model.addAttribute("comment", comment);
    	    
    	    if (!file.isEmpty()) {
    	        try {
    	            // 一時的にBase64でエンコードして表示用にする
    	            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
    	            model.addAttribute("imagePreview", base64Image);
    	        } catch (IOException e) {
    	            e.printStackTrace();
    	        }
    	    }
    	    
        return "page/BookingConfirmation"; // 同じページに戻り、結果表示
    }
}