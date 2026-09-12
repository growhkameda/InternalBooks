package com.example.internalbooks.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class DtoAuthRequest {
	@NotBlank(message = "メールアドレスは必須です")
	@Email(message = "メールアドレスの形式が正しくありません")
	@Size(max = 255, message = "文字数は255文字以下で入力してください。")
	private String mailAddress;
	
	@NotBlank(message = "パスワードは必須です")
    private String password;
	
}
