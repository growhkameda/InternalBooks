package com.example.internalbooks.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internalbooks.dto.DtoUserRegistration;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.repository.TUserRepository;
import com.example.internalbooks.repository.MDepartmentRepository;

@Service
@Transactional
/**
 * MUserテーブルに対してどんな操作をしていくかをMUserリポジトリを介して制御していくサービス
 */
public class TUserService implements UserDetailsService {

    //DI用フィールド
    private final TUserRepository tUserRepository;
    private final MDepartmentRepository mDepartmentRepository;
    private final PasswordEncoder passwordEncoder;
    
    //コンストラクタインジェクション
    public TUserService(TUserRepository tUserRepository, MDepartmentRepository mDepartmentRepository,PasswordEncoder passwordEncoder) {
        this.tUserRepository = tUserRepository;
        this.mDepartmentRepository = mDepartmentRepository;
        this.passwordEncoder= passwordEncoder;
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
     */
    public String getDepartmentNameById(Integer departmentId) {
        if (departmentId == null) {
            return "未設定";
        }

        try {
            // リポジトリを使用してデータベースから部門名を取得
            Optional<String> departmentName = mDepartmentRepository.findNameById(departmentId);

            return departmentName.orElse("不明");

        } catch (NumberFormatException e) {
            // 数値に変換できない場合
            return "不明";
        } catch (Exception e) {
            // その他のエラーの場合
            return "Error";
        }
    }


    /**
     * ユーザーの所属課を取得する
     * DBへのアクセス回数が多いためパフォーマンス上げるならJOINクエリとかRepositoryに追加するといい！
     */
    public List<TUserEntity> getUserDepartmentName() {
        // 全ユーザー情報を取得
        List<TUserEntity> users = getAllUsers();

        // 各ユーザーの所属課を取得
        for (TUserEntity user : users) {
            String departmentName = getDepartmentNameById(user.getDepartmentId());
            user.setDepartmentName(departmentName);
        }

        return users;
    }
    
    /**
     *所属課と所属課IDの対応表
     */ 
    private static final Map<String,Integer> DEPT_MAP = Map.of(
    		"開発課", 1,
    		"評価検証課", 2,
    		"ITサポート課",3,
    		"営業課", 4  		
    		);
    /**
     *所属課をIntegerへ変換
     */ 
    public void userDepartmentId(DtoUserRegistration userdto) {
    	Integer depmId = DEPT_MAP.getOrDefault(
                userdto.getDepartmentId(),
                5
        );
        userdto.setDepartmentNumber(depmId);
    }
    	
    
    /**
     * ユーザー情報をDBへ保存するメソッド
     */
    public TUserEntity userRegistration(DtoUserRegistration dtuser) {
    	TUserEntity tuser = new TUserEntity();
    	tuser.setUserId(dtuser.getUserIdAsIntger());
    	tuser.setName(dtuser.getName());
    	tuser.setMailAddress(dtuser.getMailAddress());
    	String hash = passwordEncoder.encode(dtuser.getPassword());
    	tuser.setPassword(hash);
    	tuser.setDepartmentId(dtuser.getDepartmentNumber());
    	tuser.setRole(dtuser.getRole());
    	tuser.setDeleteFlg(dtuser.getDeleteFlg());

    	tUserRepository.save(tuser);

    	return tuser;

    }

}