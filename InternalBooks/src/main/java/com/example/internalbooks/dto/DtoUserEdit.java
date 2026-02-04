package com.example.internalbooks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

@Data
public class DtoUserEdit {

	private Integer userId;

	@NotBlank(message = "名前は必須です")
	private String name;

	@NotBlank(message = "所属課は必須です")
	// regexp = "\\d+"で１文字以上の数字を表す
	@Pattern(regexp = "\\d+", message = "所属課は数字で入力してください")
	private String departmentId;

	// StringをIntegerへ変換するメソッド
	public Integer getUserIdAsIntger() {
		return Integer.valueOf(userId);
	}

	// StringをIntegerへ変換するメソッド
	public Integer getDepartmentIdAsInteger() {
		return Integer.valueOf(departmentId);
	}
}
