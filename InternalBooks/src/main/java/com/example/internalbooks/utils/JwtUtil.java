package com.example.internalbooks.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

    private String SECRET_KEY = "secret";  // 秘密鍵は本番環境ではより強固にする

    // トークンからユーザー名を取得
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    // ユーザーIDを取得する
    public Integer extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Integer.class);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ユーザー名からトークンを生成
    public String generateToken(String userName, int userId, boolean isAdmin) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);  // ユーザーIDをトークンに含める
        claims.put("isAdmin", isAdmin); // isAdminをトークンに含める
        return createToken(claims, userName);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // 10時間有効
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // トークンの有効性を検証
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
