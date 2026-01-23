package com.example.internalbooks.config;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
/**
 * 格納するパスワードをエンコードするためのクラス
 */
public class PasswordEncoderConfig {

    @Bean
    /**
     * パスワードをエンコード
     * @return エンコードされたパスワード
     */
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
