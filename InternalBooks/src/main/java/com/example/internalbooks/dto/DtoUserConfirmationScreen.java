package com.example.internalbooks.dto;

public class DtoUserConfirmationScreen {
	private String userId;
	private String name;
	private String departmantId;
	public String getDepartmantId() {
		return departmantId;
	}
	public void setDepartmantId(String departmantId) {
		this.departmantId = departmantId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
}
