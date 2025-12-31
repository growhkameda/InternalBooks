package com.example.internalbooks.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internalbooks.dto.DtoUserConfirmationScreen;
import com.example.internalbooks.dto.DtoUserEdit;
import com.example.internalbooks.dto.DtoUserRegistration;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.repository.MDepartmentRepository;
import com.example.internalbooks.repository.TUserRepository;

@Service
@Transactional
/**
 * MUserテーブルに対してどんな操作をしていくかをMUserリポジトリを介して制御していくサービス
 */
public class TUserService implements UserDetailsService {

    //DI用フィールド
    private final TUserRepository tUserRepository;
    private final MDepartmentRepository mDepartmentRepository;

    //コンストラクタインジェクション
    public TUserService(TUserRepository tUserRepository, MDepartmentRepository mDepartmentRepository) {
        this.tUserRepository = tUserRepository;
        this.mDepartmentRepository = mDepartmentRepository;
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
     * ユーザーIDからユーザー名を取得する
     * @param username ユーザ名(メールアドレス)
     * @return ユーザ情報
     */
    public String getNameByUserId(Integer userId) throws UsernameNotFoundException {
        TUserEntity user = tUserRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        return user.getName(); // 名前だ      け返す
    }
    
    /**
     * ユーザIDからTUser情報を取得するメソッド
     * @param userId ユーザID
     * @return ユーザ情報
     */
    public TUserEntity getUserById(Integer userId) throws UsernameNotFoundException {
        Optional<TUserEntity> user = tUserRepository.findById(userId); // IDでユーザーを検索
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
     * 受け取ったデータを検証し、DBの更新を行う
     */
    public TUserEntity userEditReceive(Integer userId, String name, Integer departmentId) {
        TUserEntity user = tUserRepository.findById(userId).orElseThrow();
        user.setName(name);
        user.setDepartmentId(departmentId);
        return  tUserRepository.save(user); 
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
     * ユーザー情報をDBへ保存するメソッド
     */
    public TUserEntity userRegistration(DtoUserRegistration dtuser) {
    	TUserEntity tuser = new TUserEntity();
    	tuser.setUserId(dtuser.getUserIdAsIntger());
    	tuser.setName(dtuser.getName());
    	tuser.setMailAddress(dtuser.getMailAddress());
    	tuser.setPassword(dtuser.getPassword());
    	tuser.setDepartmentId(dtuser.getDepartmentIdAsInteger());
    	tuser.setRole(dtuser.getRole());
    	tuser.setDeleteFlg(dtuser.getDeleteFlg());

    	tUserRepository.save(tuser);

    	return tuser;

    }
    
    /*
     *　編集されたユーザー情報をuserIdに紐づけて次のページに渡す
     */
    public DtoUserConfirmationScreen userConfirmationDto(Integer userId) {
    	//userId に該当するユーザーが DB にいれば取得し、いなければ即エラーにする。
        TUserEntity entity = tUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("ユーザーが存在しません: userId=" + userId));

        DtoUserConfirmationScreen dto = new DtoUserConfirmationScreen();
        dto.setUserId(entity.getUserId());
        dto.setName(entity.getName());
        dto.setDepartmentId(entity.getDepartmentId()); // または departmentName
        return dto;
    }
    
    /*
     *　編集されたユーザー情報をDBへ渡す
     */
    
    public DtoUserEdit userEditDto(Integer userId) {
    	//userId に該当するユーザーが DB にいれば取得し、いなければ即エラーにする
        TUserEntity entity = tUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("ユーザーが存在しません: userId=" + userId));

        DtoUserEdit dto = new DtoUserEdit();
        dto.setUserId(entity.getUserId());

        return dto;
    }
    
    
    /*
     *　検索画面の情報をuserIdに紐づけて次のページに渡す
     */
    public TUserEntity finishUserEdit(DtoUserEdit dtuser) {
    	TUserEntity editedUserInfo =
    		    tUserRepository.findById(dtuser.getUserIdAsIntger())
    		        .orElseThrow(() -> new RuntimeException("ユーザーが存在しません"));
    	editedUserInfo.setUserId(dtuser.getUserIdAsIntger());
    	editedUserInfo.setName(dtuser.getName());
    	editedUserInfo.setDepartmentId(dtuser.getDepartmentIdAsInteger());
    	  
    	tUserRepository.save(editedUserInfo);
    	  
    	return editedUserInfo;
    	  
    }
}