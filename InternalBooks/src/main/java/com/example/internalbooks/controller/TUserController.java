package com.example.internalbooks.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.internalbooks.dto.DtoChangePassword;
import com.example.internalbooks.exception.AuthenticationFailedException;
import com.example.internalbooks.service.AuthService;
import com.example.internalbooks.service.TBookService;
import com.example.internalbooks.service.TUserService;
import com.example.internalbooks.utils.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/user")
@Slf4j
public class TUserController extends InternalBooksController {
	
	// ロガー
	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TUserController.class);
	// DI用フィールド
	private final TUserService tUserService;
	
	// コンストラクタインジェクション
	public TUserController(JwtUtil jwtUtil, AuthService authService, TUserService tUserService, TBookService tBookService) {
		super(jwtUtil, authService, tBookService);
		this.tUserService = tUserService;
	}

	/**
	 * パスワード変更ページに遷移
	 */
	@GetMapping("/page/changePassword")
	public String changePassword(Model model) {
		logger.info("パスワード変更ページに遷移");
		model.addAttribute("changePasswordDto", new DtoChangePassword());
		return "page/changePassword";
	}
	
	/**
	 * パスワード変更処理
	 */
	@PostMapping("/action/changePassword")
	public String changePasswordAction(
			@Valid @ModelAttribute("changePasswordDto") DtoChangePassword changePasswordDto,
			BindingResult bindingResult,
			HttpSession session,
			Model model) {
		
		try {
			// JWT認証トークンの検証（共通メソッド）
			Integer loginUserId = validateTokenAndGetUserId(session);
			
			if(bindingResult.hasErrors()) {
				logger.warn("パスワード変更の入力チェックエラー。userId={}",loginUserId);
				model.addAttribute("errorMessage","パスワード変更に失敗しました。\n入力内容を確認してください。");
				return "page/changePassword";
			}
			
			tUserService.changePassword(loginUserId, changePasswordDto);
			// パスワード変更成功フラグをセッションに設定
			session.setAttribute("passswordChanged", true);
			logger.info("パスワード変更に成功しました。userId={}", loginUserId);
			return "page/changePasswordCompleted";
			
		} catch (IllegalArgumentException e) {
			logger.warn("パスワード変更失敗(不正な入力値): {}", e.getMessage());
			model.addAttribute("errorMessage",e.getMessage());
			return "page/changePassword";
		
		} catch (AuthenticationFailedException e) {
			logger.warn("パスワード変更失敗(認証エラー): {}", e.getMessage());
			model.addAttribute("errorMessage",e.getMessage());
			return "page/changePassword";
			
		} catch (Exception e) {
			logger.error("パスワード変更処理でエラーが発生しました。", e);
			return "page/changePassword";
		}
	}
	
	/**
	 * パスワード変更完了ページに遷移
	 */
	@GetMapping("/page/changePasswordCompleted")
	public String changePasswordCompleted(HttpSession session) {
		Object flag = session.getAttribute("passswordChanged");
		
		// 成功フラグがなければ、直接アクセスされたとみなし、パスワード変更ページにリダイレクト
		if (flag == null) {
			return "redirect:/user/page/changePassword";
		}
		
		session.removeAttribute("passswordChanged");
		logger.info("パスワード変更完了ページに遷移");
		return "page/changePasswordCompleted";
		
	}
}
