package com.example.internalbooks.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.internalbooks.entity.MUserEntity;
import com.example.internalbooks.repository.MUserRepository;

@Service
/**
 * MUserテーブルに対してどんな操作をしていくかをMUserリポジトリを介して制御していくサービス
 */
public class MUserService implements UserDetailsService {

    @Autowired
    private MUserRepository mUserRepository;
    
    @Override
    /**
     * ユーザ名(メールアドレス)からMUser情報を取得するメソッド
     * @param username ユーザ名(メールアドレス)
     * @return ユーザ情報
     */
    public MUserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
        MUserEntity user = mUserRepository.findByEmail(username).get(); // メールでユーザーを検索
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;  // LoginUser を返す
    }
    
    /**
     * ユーザIDからMUser情報を取得するメソッド
     * @param userId ユーザID
     * @return ユーザ情報
     */
    public MUserEntity getUserById(Integer userId) throws UsernameNotFoundException {
        Optional<MUserEntity> user = mUserRepository.findById(userId); // メールでユーザーを検索
        if (user.isEmpty()) {
        	return null;
        }
        return user.get();
    }


}
