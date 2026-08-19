package com.example.internalbooks.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internalbooks.common.Const;
import com.example.internalbooks.dto.DtoChangePassword;
import com.example.internalbooks.dto.DtoUserEdit;
import com.example.internalbooks.dto.DtoUserRegistration;
import com.example.internalbooks.entity.MDepartmentEntity;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.exception.AuthenticationFailedException;
import com.example.internalbooks.repository.MDepartmentRepository;
import com.example.internalbooks.repository.TUserRepository;

@Service
@Transactional
/**
 * MUserテーブルに対してどんな操作をしていくかをMUserリポジトリを介して制御していくサービス
 */
public class TUserService implements UserDetailsService {

	// DI用フィールド
	private final TUserRepository tUserRepository;
	private final MDepartmentRepository mDepartmentRepository;
	private final PasswordEncoder passwordEncoder;

	// コンストラクタインジェクション
	public TUserService(TUserRepository tUserRepository, MDepartmentRepository mDepartmentRepository,
			PasswordEncoder passwordEncoder) {
		this.tUserRepository = tUserRepository;
		this.mDepartmentRepository = mDepartmentRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	/**
	 * ユーザ名(メールアドレス)からTUser情報を取得するメソッド
	 *
	 * @param username ユーザ名(メールアドレス)
	 * @return ユーザ情報
	 */
	public TUserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
		TUserEntity user = tUserRepository.findByMailAddress(username).get(); // メールでユーザーを検索
		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}
		return user; // LoginUser を返す
	}
	
	/**
	 * ユーザIDからTUser情報を取得するメソッド
	 *
	 * @param userId ユーザID
	 * @return ユーザ情報
	 */
	public TUserEntity getUserById(Integer userId) {
		Optional<TUserEntity> user = tUserRepository.findById(userId); // メールでユーザーを検索
		if (user.isEmpty()) {
			return null;
		}
		return user.get();
	}

	/**
	 * 全ユーザー情報を取得するメソッド
	 *
	 * @return 全ユーザーリスト
	 */
	public List<TUserEntity> getAllUsers() {
		return tUserRepository.findAll();
	}

	/**
	 * アクティブ（論理削除されていない）ユーザー情報を取得するメソッド
	 *
	 * @return アクティブユーザーリスト
	 */
	public List<TUserEntity> getActiveUsers() {
		return tUserRepository.findByDeleteFlg(Const.DELETE_FLAG_OFF);
	}

	/**
	 * アクティブユーザーを氏名の昇順で取得する（書籍提供者プルダウン用）
	 *
	 * @return 論理削除されていないユーザーリスト（名前昇順）
	 */
	public List<TUserEntity> getActiveUsersSortedByName() {
		return tUserRepository.findByDeleteFlgOrderByNameAsc(Const.DELETE_FLAG_OFF);
	}

	/**
	 * ログインユーザを除いたアクティブユーザリストを取得する
	 *
	 * @param ログインユーザid
	 * @return アクティブユーザーリスト
	 */
	public List<TUserEntity> getUsersExceptCurrent(Integer currentUserId) {
		List<TUserEntity> activeUsers = getActiveUsers();
		List<TUserEntity> UsersExceptCurrent = new ArrayList<>();
		for (TUserEntity user : activeUsers) {
			if (!currentUserId.equals(user.getUserId())) {
				UsersExceptCurrent.add(user);
			}
		}
		return UsersExceptCurrent;
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
	 * ユーザーのIDを取得（リスト形式）
	 */
	public Optional<TUserEntity> getUserId(String userId) {
		
		//String型をInteger型へ変換
		Integer id = Integer.valueOf(userId);
		
		return tUserRepository.findById(id);
	}
	
	/**
	 * メールアドレスが既に登録済みかどうかを判定する
	 */
	public boolean isMailAddressRegistered(String mailAddress) {
	    return tUserRepository.findByMailAddress(mailAddress).isPresent();
	}

	/**
	 * ユーザーの所属課を取得（リスト形式）
	 */
	public List<TUserEntity> getUserDepartmentName(Integer currentUserId) {
		// アクティブユーザー情報を取得
		List<TUserEntity> users = getUsersExceptCurrent(currentUserId);

		// 各ユーザーの所属課を取得
		for (TUserEntity user : users) {
			String departmentName = getDepartmentNameById(user.getDepartmentId());
			user.setDepartmentName(departmentName);
		}
		return users;
	}

	/**
	 * ユーザーの所属課を取得する(単体取得)
	 */
	public TUserEntity getUserWithDepartmentNameById(Integer userId) {
		// ユーザー情報を取得
		TUserEntity user = getUserById(userId);

		// 各ユーザーの所属課を取得
		if (user != null) {
			String departmentName = getDepartmentNameById(user.getDepartmentId());
			user.setDepartmentName(departmentName);
		}
		return user;
	}

	/**
	 * ユーザーIDからDtoUserEditを生成して返す
	 */
	public DtoUserEdit getUserEditDtoById(Integer userId) {
		TUserEntity user = getUserById(userId);
		if (user == null) return null;
		DtoUserEdit dto = new DtoUserEdit();
		dto.setUserId(user.getUserId());
		dto.setName(user.getName());
		dto.setMailAddress(user.getMailAddress());
		dto.setDepartmentId(String.valueOf(user.getDepartmentId()));
		return dto;
	}

    /**
     * ユーザー情報をDBへ保存するメソッド
     */
    public DtoUserRegistration userRegistration(DtoUserRegistration dtuser) {

    	 MDepartmentEntity deptId = mDepartmentRepository
		                           .findIdByName((dtuser.getDepartmentId()))
                                   .orElseThrow(() -> new IllegalArgumentException("部署なし"));

    	TUserEntity tuser = new TUserEntity();
    	tuser.setUserId(dtuser.getUserIdAsInteger());
    	tuser.setName(dtuser.getName());
    	tuser.setMailAddress(dtuser.getMailAddress());
    	String hash = passwordEncoder.encode(dtuser.getMailAddress());
    	tuser.setPassword(hash);
    	tuser.setDepartmentId(deptId.getId());
    	tuser.setRole(dtuser.getRole());
    	tuser.setDeleteFlg(dtuser.getDeleteFlg());

		tUserRepository.save(tuser);

		DtoUserRegistration result = new DtoUserRegistration();
		result.setUserId(String.valueOf(tuser.getUserId()));
		result.setName(tuser.getName());
		result.setMailAddress(tuser.getMailAddress());
		result.setDepartmentId(dtuser.getDepartmentId());
		return result;

	}

	/**
	 * ユーザー編集保存
	 */
	public void updateUser(DtoUserEdit userDto) {
		// 既存ユーザーの取得（いなければエラー）
		TUserEntity existingUser = getUserById(userDto.getUserId());
		if (existingUser == null) {
			throw new RuntimeException("ユーザーが存在しません");
		}
		Optional<TUserEntity> otherUserOpt = tUserRepository.findByMailAddress(userDto.getMailAddress());

		// メールアドレス重複チェック（自分以外の誰かが使っていないか）
		if (otherUserOpt.isPresent()) {
			TUserEntity otherUser = otherUserOpt.get();
			// 見つかった人が自分以外のIDなら、それは「他人のメルアド」なのでエラー
			if (!otherUser.getUserId().equals(existingUser.getUserId())) {
				throw new IllegalArgumentException("このメールアドレスは既に他のユーザーに使用されています");
			}
		}

		// 編集のためTUserEntityクラスに詰め替え
		existingUser.setName(userDto.getName());
		existingUser.setMailAddress(userDto.getMailAddress());
		existingUser.setDepartmentId(userDto.getDepartmentIdAsInteger());

		// パスワードを現在のEmailで上書き（常に同期）
		existingUser.setPassword(passwordEncoder.encode(userDto.getMailAddress()));

		// DBへ書き込み
		tUserRepository.save(existingUser);

	}

	/**
	 * ユーザー削除
	 */
	public void deleteUser(Integer userId) {
		tUserRepository.DeleteUserById(userId);
	}

	/**
	 * 課一覧リストを取得
	 */
	public List<MDepartmentEntity> getAllDepartments() {
		return mDepartmentRepository.findAll();
	}
	
	/**
	 * パスワード変更
	 */
	@Transactional
	public void changePassword(Integer loginUserId, DtoChangePassword changePasswordDto) {
		Optional<TUserEntity> user = tUserRepository.findById(loginUserId);
		if (user.isEmpty()) {
			throw new AuthenticationFailedException("該当するユーザーが存在しません");
		}
		
		if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())) {
			throw new IllegalArgumentException("新しいパスワードと確認用パスワードが一致しません");
		}
		
		validatePasswordPolicy(changePasswordDto.getNewPassword());
		
		TUserEntity userEntity = user.get();
		userEntity.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
		
		tUserRepository.save(userEntity);
	}
	
	/**
	 * パスワードポリシーの検証
	 */
	private void validatePasswordPolicy(String password) {
		if (password.length() < 8) {
			throw new IllegalArgumentException("パスワードは8文字以上である必要があります");
		}
		if (password.matches(".*\\s.*")) {
			throw new IllegalArgumentException("パスワードには空白文字を含めることはできません");
		}
		if (!password.matches(".*[A-Z].*")) {
			throw new IllegalArgumentException("パスワードには少なくとも1つの大文字が含まれている必要があります");
		}
		if (!password.matches(".*[a-z].*")) {
			throw new IllegalArgumentException("パスワードには少なくとも1つの小文字が含まれている必要があります");
		}
		if (!password.matches(".*\\d.*")) {
			throw new IllegalArgumentException("パスワードには少なくとも1つの数字が含まれている必要があります");
		}
		if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
			throw new IllegalArgumentException("パスワードには少なくとも1つの特殊文字が含まれている必要があります");
		}
	}
}