package com.example.internalbooks.service;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.repository.TUserRepository;
import com.example.internalbooks.dto.DtoUserDisplay;

@Service
@Transactional
/**
 * MUserテーブルに対してどんな操作をしていくかをMUserリポジトリを介して制御していくサービス
 */
public class TUserService implements UserDetailsService {

    //ロガー
    private static final Logger logger = LoggerFactory.getLogger(TUserService.class);
    
    //DI用フィールド
    private final TUserRepository tUserRepository;

    //コンストラクタインジェクション
    public TUserService(TUserRepository tUserRepository) {
        this.tUserRepository = tUserRepository;
    }
    

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

    /**
     * 全ユーザー情報を取得するメソッド
     * @return 全ユーザーリスト
     */
    public List<TUserEntity> getAllUsers() {
        return tUserRepository.findAll();
    }

    /**
     * 部門IDから部門名を取得する
     * (現在は仮で設定)
     */
    public String getDepartmentNameById(String departmentId) {
        if (departmentId == null) {
            return "未設定";
        }
        
        switch (departmentId) {
            case "1": return "11課";
            case "2": return "22課";
            case "3": return "33課";
            case "4": return "44課";
            case "5": return "55課";
            case "6": return "66課";
            case "7": return "77課";
            case "8": return "88課";
            case "9": return "99課";
            case "10": return "100課";
            default: return "不明";
        }
    }
    
    /**
     * 管理者画面用：部門名を含むユーザー一覧を取得
     * ビジネスロジック（部門名の変換）をServiceで処理
     * @return 部門名を含むユーザー表示用DTOのリスト
     */
    public List<DtoUserDisplay> getAllUsersWithDepartmentName() {
        List<TUserEntity> users = tUserRepository.findAll();
        List<DtoUserDisplay> userDisplayList = new ArrayList<>();
        
        for (TUserEntity user : users) {
            // 部門名の変換処理をServiceで実行
            String departmentName = getDepartmentNameById(user.getDepartmentId());
            
            // DTOに変換
            DtoUserDisplay userDisplay = new DtoUserDisplay(
                user.getUserId(),
                user.getName(),
                user.getMailAddress(),
                user.getEmployeeId(),
                user.getDepartmentId(),
                departmentName,
                user.isAdmin()
            );
            userDisplayList.add(userDisplay);
        }
        
        logger.info("部門名を含むユーザー一覧を取得しました。件数: {}", userDisplayList.size());
        return userDisplayList;
    }

}
