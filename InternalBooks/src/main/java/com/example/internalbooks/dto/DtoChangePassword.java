package com.example.internalbooks.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class DtoChangePassword {
	@NotBlank(message = "メールアドレスは必須です")
	@Email(message = "メールアドレスの形式が正しくありません")
	private String mailAddress;
	
	@NotBlank(message = "新しいパスワードは必須です")
	@Size(min = 8, message = "新しいパスワードは8文字以上で入力してください。")
	private String newPassword;

	@NotBlank(message = "新しいパスワードの確認は必須です")
	private String confirmNewPassword;
}
