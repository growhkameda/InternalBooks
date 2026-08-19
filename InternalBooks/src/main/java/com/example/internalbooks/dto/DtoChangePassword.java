package com.example.internalbooks.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class DtoChangePassword {
	// 新しいパスワード
	@NotBlank
	private String newPassword;
	
	// 新しいパスワード（確認用）
	@NotBlank
	private String confirmNewPassword;
}
