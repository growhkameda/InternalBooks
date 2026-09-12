package com.example.internalbooks.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import com.example.internalbooks.filter.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
/**
 * 認証系のセキュリティ設定のためのクラス
 */
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService; // UserDetailsService を Autowire

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    /**
     * URLによって認証をチェックするかなどのフィルタを設定するためのメソッド
     */
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRFを無効化
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/", "/page/login", "/action/login", "/webjars/**",
                                "/logo/**", "/favicon.ico", "/images/**", "/js/**", "/actuator/health")
                        .permitAll() // 認証不要のエンドポイント

                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") // 管理者専用エンドポイント

                        .anyRequest().authenticated() // 他のエンドポイントは認証が必要
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(loginRedirectEntryPoint("セッションが切れました。再度ログインしてください。"))
                        .accessDeniedHandler(loginRedirectAccessDeniedHandler("管理者権限が必要です。")))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // フィルターを追加;

        return http.build();
    }

    /**
     * 未認証アクセス時にログインページへリダイレクトするエントリーポイント
     */
    private AuthenticationEntryPoint loginRedirectEntryPoint(String message) {
        return (request, response, authException) -> redirectToLoginWithMessage(request, response, message);
    }

    /**
     * 権限不足アクセス時にログインページへリダイレクトするハンドラ
     */
    private AccessDeniedHandler loginRedirectAccessDeniedHandler(String message) {
        return (request, response, accessDeniedException) -> redirectToLoginWithMessage(request, response, message);
    }

    private void redirectToLoginWithMessage(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        String loginPath = request.getContextPath() + "/page/login";

        // errorMessageをFlash属性としてセッションに保持し、リダイレクト先のログイン画面で表示する
        FlashMap flashMap = new FlashMap();
        flashMap.put("errorMessage", message);
        flashMap.setTargetRequestPath(loginPath);
        new SessionFlashMapManager().saveOutputFlashMap(flashMap, request, response);

        response.sendRedirect(loginPath);
    }

    @Bean
    /**
     * 認証情報を管理するためのメソッド
     */
    protected AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder); // PasswordEncoder を設定

        return authenticationManagerBuilder.build();
    }

    @Bean
    /**
     * JWT認証情報のフィルタを返却するためのメソッド
     */
    protected JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(); // JWTフィルターを定義
    }
}
