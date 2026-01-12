package com.example.internalbooks.dto;

import lombok.Data;

@Data
public class DtoUserConfirmationScreen {
    private final Integer userId;
    private final String name;
    private final String departmentName;

    public DtoUserConfirmationScreen(
        Integer userId,
        String name,
        String departmentName
    ) {
        this.userId = userId;
        this.name = name;
        this.departmentName = departmentName;
    }
}
