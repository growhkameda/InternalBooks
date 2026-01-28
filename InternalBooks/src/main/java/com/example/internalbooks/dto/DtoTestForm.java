package com.example.internalbooks.dto;

import lombok.Data;

/**
 * Test Sample
 */
@Data
// Getter / Setter
public class DtoTestForm {
    private String yaName;
    private String yaEmail;
    private String yaAddress;
    // int型だと色々と不都合なのでString型
    private String yaPhoneNumber;
}