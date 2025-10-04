package com.example.internalbooks.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

@Data
public class DtoUserRegistration {
	
	@NotBlank(message = "ユーザーIDは必須です")
//	regexp = "\\d+"で１文字以上の数字を表す
	@Pattern(regexp = "\\d+", message = "社員IDは数字で入力してください")
	private String userId;
	
	@NotBlank(message = "名前は必須です")
	private String name;
	
	@NotBlank(message = "メールアドレスは必須です")
	@Email(message = "メールアドレスの形式が正しくありません")
	private String mailAddress;
	
	@NotBlank(message = "所属課は必須です")
//	regexp = "\\d+"で１文字以上の数字を表す
	@Pattern(regexp = "\\d+", message = "所属課は数字で入力してください")
	private String departmentId;
	
	@NotBlank(message = "パスワードは必須です")
	private String password;
	
//	初期値の設定
	private Integer role = 0;

//	初期値の設定
	private Integer deleteFlg = 0;
	
//	StringをIntegerへ変換するメソッド
	public Integer getUserIdAsIntger() {
		return Integer.valueOf(userId);
	}
	
//	StringをIntegerへ変換するメソッド
	public Integer getDepartmentIdAsInteger() {
		return Integer.valueOf(departmentId);
	}

}
