package com.example.internalbooks.dto;

import lombok.Data;

@Data
public class DtoUserConfirmationScreen {
	private Integer userId;
	private String name;
	private Integer departmentId;

	// StringをIntegerへ変換するメソッド
	public String getDepartmentIdAsString() {
		return String.valueOf(departmentId);
	}
}