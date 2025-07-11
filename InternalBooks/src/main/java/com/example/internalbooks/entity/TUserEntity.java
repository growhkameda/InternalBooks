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
    
    @Column(name = Const.MAILADDRESS)
    private String mailAddress;
    
    @Column(name = Const.PASSWORD)
    private String password;
    
    @Column(name = Const.ROLE)
    private Integer role;

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
