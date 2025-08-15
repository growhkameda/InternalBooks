package com.example.internalbooks.entity;

import java.util.Collection;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.internalbooks.common.Const;

import lombok.Data;

@Data
@Entity
@Table(name = "t_user")
/**
 * MUserテーブルからデータを受け取るためのEntity
 */
public class TUserEntity implements UserDetails {
    @Id
    @Column(name = Const.USER_ID)
    private Integer userId;

    @Column(name = Const.NAME)
    private String name;
    
    @Column(name = Const.MAILADDRESS)
    private String mailAddress;
    
    @Column(name = Const.PASSWORD)
    private String password;
    
    @Column(name = Const.ROLE)
    private Integer role;
    
    @Column(name = Const.DEPARTMENT_ID)
    private Integer departmentId;
    
    @Column(name = Const.DELETE_FLG)
    private Integer deleteFlg;

    /**
     * department_idを課名に変換するメソッド
     */
    public String getDepartmentName() {
        if (departmentId == null) {
            return "未設定";
        }
        
        switch (departmentId) {
            case 1: return "11課";
            case 2: return "22課";
            case 3: return "33課";
            case 4: return "44課";
            case 5: return "55課";
            case 6: return "66課";
            case 7: return "77課";
            case 8: return "88課";
            case 9: return "99課";
            case 10: return "100課";
            default: return "不明";
        }
    }

    @Override
    /**
     * 権限の取得
     * @return 権限情報
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    /**
     * メールアドレスをユーザー名として取得
     * @return ユーザ名(メールアドレス)
     */
    public String getUsername() {
        return mailAddress;
    }

    @Override
    /**
     * アカウントが期限切れでないことを取得
     */
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    /**
     * アカウントがロックされていないことを取得
     */
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    /**
     * 認証情報が期限切れではないことを取得
     */
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    /**
     * アカウントが有効であることを取得
     */
    public boolean isEnabled() {
        return true;
    }
}
