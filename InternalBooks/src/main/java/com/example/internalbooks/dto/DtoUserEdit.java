package com.example.internalbooks.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class DtoUserEdit {

    private Integer userId;

    @NotBlank(message = "名前を入力してください。")
    @Size(max = 255, message = "名前は255文字以内で入力してください")
    private String name;

    @NotBlank(message = "メールアドレスを入力してください。")
    @Email(message = "メールアドレスの形式が正しくありません。")
    private String mailAddress;

    @NotBlank(message = "所属課を選択してください。")
    @Pattern(regexp = "\\d+", message = "所属課の選択が不正です。")
    private String departmentId;

    private String departmentName;

    // StringをIntegerへ変換するメソッド
    public Integer getDepartmentIdAsInteger() {
        return (departmentId != null && !departmentId.isEmpty()) ? Integer.valueOf(departmentId) : null;
    }
}
