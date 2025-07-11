package com.example.internalbooks.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.repository.TUserRepository;

@Service
@Transactional
/**
 * MUserテーブルに対してどんな操作をしていくかをMUserリポジトリを介して制御していくサービス
 */
public class TUserService implements UserDetailsService {

    @Autowired
    private TUserRepository tUserRepository;
    
    @Override
    /**
     * ユーザ名(メールアドレス)からTUser情報を取得するメソッド
     * @param username ユーザ名(メールアドレス)
     * @return ユーザ情報
     */
    public TUserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
        TUserEntity user = tUserRepository.findByMailAddress(username).get(); // メールでユーザーを検索
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;  // LoginUser を返す
    }
    
    /**
     * ユーザIDからTUser情報を取得するメソッド
     * @param userId ユーザID
     * @return ユーザ情報
     */
    public TUserEntity getUserById(Integer userId) throws UsernameNotFoundException {
        Optional<TUserEntity> user = tUserRepository.findById(userId); // メールでユーザーを検索
        if (user.isEmpty()) {
        	return null;
        }
        return user.get();
    }


}
