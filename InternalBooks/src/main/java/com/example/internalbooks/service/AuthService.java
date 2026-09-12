package com.example.internalbooks.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.internalbooks.dto.DtoAuthRequest;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.exception.AuthenticationFailedException;
import com.example.internalbooks.utils.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {
    
    //DI用フィールド
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    //コンストラクタインジェクション
    public AuthService(UserDetailsService userDetailsService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * ユーザー認証を行い、JWTアクセストークンを発行する。
     * * @param authRequest ログインリクエストデータ（メールアドレス、パスワード）
     * @return 生成されたJWTトークン
     * @throws AuthenticationFailedException 認証に失敗した場合、またはアカウントが無効な場合
     */
	public String login(DtoAuthRequest authRequest) {
		TUserEntity loginUser;
		try {
			// 認証を試みる
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getMailAddress(), authRequest.getPassword()));

			// LoginUserを取得
			loginUser = (TUserEntity) userDetailsService.loadUserByUsername(authRequest.getMailAddress());

		} catch (AuthenticationException e) {
			// 認証失敗（パスワード間違いやユーザー不在）
			log.warn("[認証失敗] メールアドレスまたはパスワードの不一致。Mail: {}", authRequest.getMailAddress());
	        throw new AuthenticationFailedException("メールアドレスまたはパスワードが正しくありません。");
		}

		// 削除されたユーザかの確認
		if (loginUser.getDeleteFlg() != 0) {
			log.warn("[認証拒否] 削除済みユーザーによるログイン試行。Mail: {}", authRequest.getMailAddress());
			throw new AuthenticationFailedException("このアカウントは現在利用できません。");
		}
		
		// ユーザーIDを取得
		Integer userId = loginUser.getUserId(); 
		// ユーザーのroleを取得
		Integer role = loginUser.getRole(); // ここでroleを取得（例えば、1が管理者、0が一般ユーザー）
		// roleが1の場合、isAdminをtrueに設定
		boolean isAdmin = (role == 1);

		// ユーザー名とユーザーIDでトークンを生成
		final String token = jwtUtil.generateToken(authRequest.getMailAddress(), userId, isAdmin);
		return token;
		
	}

}
