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
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    @GetMapping("/test/kimata")
    public String kimata() {
    	logger.info("★★★★★★★★★★★kimata() にアクセスされました");
        return "test/kimata";
    }
    @GetMapping("/test/matunaga")
    public String matunaga() {
    	logger.info("★★★★★★★★★★★matunaga() にアクセスされました");
        return "test/matunaga";
    }
    @GetMapping("/test/mutou")
    public String mutou() {
    	logger.info("★★★★★★★★★★★mutou() にアクセスされました");
        return "test/mutou";
    }
    @GetMapping("/test/sano")
    public String sano() {
    	logger.info("★★★★★★★★★★★sano() にアクセスされました");
        return "test/sano";
    }
    @GetMapping("/test/kameda")
    public String kameda() {
    	logger.info("★★★★★★★★★★★kameda() にアクセスされました");
        return "test/kameda";
    }
    @GetMapping("/test/amemiya")
    public String amemiya() {
    	logger.info("★★★★★★★★★★★amemiya() にアクセスされました");
        return "test/amemiya";
    }
    @GetMapping("/test/qr_test")
    public String qrTest() {
    	logger.info("★★★★★★★★★★★qrTest() にアクセスされました");
        return "test/qr_test";
    }
    
    @GetMapping("/page/login")
    public String Login() {
    	logger.info("★★★★★★★★★★★Loginされました");
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
     * QRコードスキャナーページを表示
     * 社内図書館システム用のQRコード読み取り機能を提供
     * 
     * 機能概要:
     * - カメラを使用したQRコード読み取り
     * - 読み取り後、自動的にダミーページに遷移
     * - 認証が必要（JWT認証）
     * 
     * @param session HTTPセッション（JWT認証トークン取得用）
     * @param model モデル
     * @param redirectAttributes 認証失敗時のリダイレクト用
     * @return QRスキャナーページのテンプレート名 または 認証エラー時のリダイレクト
     */
    @GetMapping("/page/qr_scanner")
    public String qrScanner(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    	try {
    		// JWT認証トークンの検証
    		String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
            logger.info("QRスキャナーページにアクセスされました");

            return "page/qr_scanner";
    	}
    	catch (Exception e) {
    		// 認証失敗時はログインページにリダイレクト
            return error(redirectAttributes);
    	}
    }
    
    /**
     * QRコード読み取り結果表示ページ（ダミーページ）
     * 社内図書館システム用のQRスキャン結果表示機能
     * 
     * 機能概要:
     * - QRスキャナーから遷移してきたQRデータを表示
     * - URLの場合は外部リンクとして開くボタンを提供
     * - データのコピー機能を提供
     * - QRスキャナーページに戻る機能を提供
     * - 将来的にはQRパラメータに応じた表示内容の変更が可能
     * 
     * データ取得方法:
     * 1. URLパラメータから取得 (GETメソッド)
     * 2. セッションから取得 (POSTリダイレクト方式)
     * 
     * @param qrData QRコードから読み取ったデータ（URLパラメータ）
     * @param session HTTPセッション（JWT認証トークン・セッションデータ取得用）
     * @param model Thymeleafテンプレートに渡すモデル
     * @param redirectAttributes 認証失敗時のリダイレクト用
     * @return ダミーページのテンプレート名 または 認証エラー時のリダイレクト
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

            return "page/dummy";
    	}
    	catch (Exception e) {
    		// 認証失敗時はログインページにリダイレクト
            return error(redirectAttributes);
    	}
    }
    
    /**
     * QRコード読み取り結果をPOSTで受信してダミーページにリダイレクト
     * POST-Redirect-GET パターンによる安全なデータ遷移を実現
     * 
     * 機能概要:
     * - QRスキャナーページからPOSTでQRデータを受信
     * - データをセッションに一時保存
     * - ダミーページにリダイレクト
     * - ダミーページでセッションからデータを取得して表示
     * 
     * メリット:
     * - ブラウザの戻る/更新時の重複送信を防止
     * - セッション利用により確実なデータ引き継ぎ
     * 
     * @param qrData QRコードから読み取ったデータ
     * @param session HTTPセッション（JWT認証トークン・データ保存用）
     * @param redirectAttributes 認証失敗時のリダイレクト用
     * @return ダミーページへのリダイレクト または 認証エラー時のリダイレクト
     */
    @PostMapping("/action/qr_result")
    public String qrResult(@RequestParam(name = "qrData") String qrData, HttpSession session, RedirectAttributes redirectAttributes) {
    	try {
    		// JWT認証トークンの検証
    		String token = (String) session.getAttribute("token");
            jwtUtil.extractUserId(token);
            
            // QRデータをセッションに一時保存（POST-Redirect-GET パターン）
            session.setAttribute("qrData", qrData);
            
            return "redirect:/page/dummy";
    	}
    	catch (Exception e) {
    		// 認証失敗時はログインページにリダイレクト
            return error(redirectAttributes);
    	}
    }
    
    private String error(RedirectAttributes redirectAttributes) {
    	redirectAttributes.addFlashAttribute("errorMessage", "セッションが切れました。再度ログインしてください。");
        return "redirect:/page/login";
    }

}