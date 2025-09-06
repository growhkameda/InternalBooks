package com.example.internalbooks.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public class DtoUserRegistration {
	
	@NotBlank(message = "ユーザーIDは必須です")
	private Integer userId;
	@NotBlank(message = "名前は必須です")
	private String name;
	@NotBlank(message = "メールアドレスは必須です")
	private String mailAddress;
	@NotBlank(message = "所属課は必須です")
	private String departmentId;
	@NotBlank(message = "パスワードは必須です")
	private String password;
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMailAddress() {
		return mailAddress;
	}
	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
