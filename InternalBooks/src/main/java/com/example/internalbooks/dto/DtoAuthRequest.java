package com.example.internalbooks.dto;

import lombok.Data;

@Data
public class DtoAuthRequest {
	private String mailAddress;
    private String password;
}
