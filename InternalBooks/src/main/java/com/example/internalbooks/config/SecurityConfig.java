package com.example.internalbooks.config;

import java.util.List;

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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.internalbooks.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
/**
 * 認証系のセキュリティ設定のためのクラス
 */
public class SecurityConfig {
	
    @Autowired
    private UserDetailsService userDetailsService;  // UserDetailsService を Autowire
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Bean
    /**
     * URLによって認証をチェックするかなどのフィルタを設定するためのメソッド
     */
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // CSRFを無効化
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/page/**", "/action/**","/test/**", "/webjars/**", "/logo/**","/favicon.ico").permitAll()  // 認証不要のエンドポイント
                .anyRequest().authenticated()  // 他のエンドポイントは認証が必要
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED )
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // フィルターを追加;

        	http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        
        return http.build();
    }
    
    @Bean
    /**
     * 認証情報を管理するためのメソッド
     */
    protected AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        
        authenticationManagerBuilder
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder);  // PasswordEncoder を設定

        return authenticationManagerBuilder.build();
    }
        
    @Bean
    /**
     * JWT認証情報のフィルタを返却するためのメソッド
     */
    protected JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(); // JWTフィルターを定義
    }
    
    @Bean
    /**
     * CORSアクセスのための設定をするメソッド
     */
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of( "*")); // 許可するオリジン
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 許可するメソッド
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // 許可するヘッダー
        configuration.setAllowCredentials(true); // 認証情報を含むリクエストを許可

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 全エンドポイントに適用

        return source;
    }
}
