package com.example.internalbooks.exception;

/**
 * 認証失敗（パスワード相違、ユーザー不在、削除済みユーザーなど）を通知する例外
 */
public class AuthenticationFailedException extends RuntimeException {
    
    // コンストラクタ：エラーメッセージを親クラス（RuntimeException）に渡す
    public AuthenticationFailedException(String message) {
        super(message);
    }
}