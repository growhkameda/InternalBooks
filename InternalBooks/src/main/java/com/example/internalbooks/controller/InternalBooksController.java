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
<<<<<<< HEAD
    @GetMapping("/test/qr_test")
    public String qrTest() {
    	logger.info("★★★★★★★★★★★qrTest() にアクセスされました");
        return "test/qr_test";
    }
=======
    
    @GetMapping("/page/login")
    public String Login() {
    	logger.info("★★★★★★★★★★★Loginされました");
        return "page/login";
    }
    
    @PostMapping("/action/login")
    public String login(@RequestParam String mailAddress, @RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes) {
    	
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
    
    private String error(RedirectAttributes redirectAttributes) {
    	redirectAttributes.addFlashAttribute("errorMessage", "セッションが切れました。再度ログインしてください。");
        return "redirect:/page/login";
    }

>>>>>>> login
}