package com.example.internalbooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.internalbooks.dto.DtoAuthRequest;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.utils.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    public String login(DtoAuthRequest authRequest) {
        try {
            // 認証を試みる
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getMailAddress(), authRequest.getPassword())
            );

            // LoginUserを取得
            TUserEntity loginUser = (TUserEntity) userDetailsService.loadUserByUsername(authRequest.getMailAddress());
            Integer userId = loginUser.getUserId();  // ユーザーIDを取得

            // ユーザーのroleを取得
            Integer role = loginUser.getRole();  // ここでroleを取得（例えば、1が管理者、0が一般ユーザー）

            // roleが1の場合、isAdminをtrueに設定
            boolean isAdmin = (role == 1);

            // ユーザー名とユーザーIDでトークンを生成
            final String token = jwtUtil.generateToken(authRequest.getMailAddress(), userId, isAdmin);
            return token;
        } catch (Exception e) {
            throw new RuntimeException("Invalid credentials"); // 認証失敗時のエラーメッセージ
        }
    }

}
