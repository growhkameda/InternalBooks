package com.example.internalbooks.dto;

import lombok.Data;

@Data
public class DtoUserConfirmationScreen {
	private String userId;
	private String name;
	private String departmentId;{
	}
//	StringをIntegerへ変換するメソッド
	public Integer getUserIdAsIntger() {
		return Integer.valueOf(userId);
	}

	
//	StringをIntegerへ変換するメソッド
	public Integer getDepartmentIdAsInteger() {
		return Integer.valueOf(departmentId);
	}
}
