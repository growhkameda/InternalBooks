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
@Table(name = "m_user")
/**
 * MUserテーブルからデータを受け取るためのEntity
 */
public class MUserEntity implements UserDetails {
    @Id
    @Column(name = Const.ID)
    private Integer id;
    
    @Column(name = Const.EMAIL)
    private String email;
    
    @Column(name = Const.PASSWORD)
    private String password;
    
    @Column(name = Const.ROLE_ID)
    private Integer roleId;

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
        return email;
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
