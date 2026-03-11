package com.example.internalbooks.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class DtoAuthRequest {
	@NotBlank(message = "メールアドレスは必須です")
	@Email(message = "メールアドレスの形式が正しくありません")
	private String mailAddress;
	
	@NotBlank(message = "パスワードは必須です")
    private String password;
}
