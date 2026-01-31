package com.example.internalbooks.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.internalbooks.common.Const;
import com.example.internalbooks.dto.DtoUserRegistration;
import com.example.internalbooks.entity.MDepartmentEntity;
import com.example.internalbooks.entity.TUserEntity;
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
	public TUserService(TUserRepository tUserRepository, MDepartmentRepository mDepartmentRepository, PasswordEncoder passwordEncoder) {
		this.tUserRepository = tUserRepository;
		this.mDepartmentRepository = mDepartmentRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public TUserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
		TUserEntity user = tUserRepository.findByMailAddress(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
		return user;
	}

	/**
	 * ユーザIDからTUser情報を取得するメソッド
	 */
	public TUserEntity getUserById(Integer userId) {
		return tUserRepository.findById(userId).orElse(null);
	}

	public List<TUserEntity> getAllUsers() {
		return tUserRepository.findAll();
	}

	/**
	 * アクティブ（論理削除されていない）ユーザー情報を取得
	 */
	public List<TUserEntity> getActiveUsers() {
		return tUserRepository.findByDeleteFlg(Const.DELETE_FLAG_OFF);
	}

	/**
	 * ログインユーザを除いたアクティブユーザリストを取得
	 */
	public List<TUserEntity> getUsersExceptCurrent(Integer currentUserId) {
		List<TUserEntity> activeUsers = getActiveUsers();
		List<TUserEntity> usersExceptCurrent = new ArrayList<>();
		for (TUserEntity user : activeUsers) {
			if (!currentUserId.equals(user.getUserId())) {
				usersExceptCurrent.add(user);
			}
		}
		return usersExceptCurrent;
	}

	/**
	 * 部門IDから部門名を取得
	 */
	public String getDepartmentNameById(Integer departmentId) {
		if (departmentId == null) {
			return "未設定";
		}
		try {
			Optional<String> departmentName = mDepartmentRepository.findNameById(departmentId);
			return departmentName.orElse("不明");
		} catch (Exception e) {
			return "Error";
		}
	}

	/**
	 * ユーザーの所属課を取得（リスト形式）
	 */
	public List<TUserEntity> getUserDepartmentName(Integer currentUserId) {
		List<TUserEntity> users = getUsersExceptCurrent(currentUserId);
		for (TUserEntity user : users) {
			user.setDepartmentName(getDepartmentNameById(user.getDepartmentId()));
		}
		return users;
	}

	/**
	 * ユーザーの所属課を取得（単体取得）
	 */
	public TUserEntity getUserWithDepartmentNameById(Integer userId) {
		TUserEntity user = getUserById(userId);
		if (user != null) {
			user.setDepartmentName(getDepartmentNameById(user.getDepartmentId()));
		}
		return user;
	}

	/**
	 * ユーザー登録
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
		return tUserRepository.save(tuser);
	}

	/**
	 * ユーザー編集保存（あなたのパスワード更新機能を含む版）
	 */
	public void updateUser(TUserEntity userDto, String currentPwd, String newPwd) {
		TUserEntity existingUser = tUserRepository.findById(userDto.getUserId())
				.orElseThrow(() -> new RuntimeException("ユーザーが存在しません"));

		// パスワード更新ロジック
		if (StringUtils.hasText(currentPwd) && StringUtils.hasText(newPwd)) {
			if (passwordEncoder.matches(currentPwd, existingUser.getPassword())) {
				existingUser.setPassword(passwordEncoder.encode(newPwd));
			} else {
				throw new IllegalArgumentException("現在のパスワードが正しくありません");
			}

			existingUser.setName(userDto.getName());
			existingUser.setMailAddress(userDto.getMailAddress());
			existingUser.setDepartmentId(userDto.getDepartmentId());

			tUserRepository.save(existingUser);
		}

		existingUser.setName(userDto.getName());
		existingUser.setMailAddress(userDto.getMailAddress());
		existingUser.setDepartmentId(userDto.getDepartmentId());
		tUserRepository.save(existingUser);
	}

	/**
	 * ユーザー削除
	 */
	public void deleteUser(Integer userId) {
		tUserRepository.DeleteUserById(userId);
	}
	
	/**
	 * 全部門リスト取得
	 */
	public List<MDepartmentEntity> getAllDepartments() {
		return mDepartmentRepository.findAll();
	}

	// --- 以下、Kevinさんが追加したメソッド（共存のため残す） ---

	public TUserEntity userConfirmationDto(Integer userId) {
		return getUserWithDepartmentNameById(userId);
	}

	public DtoUserEdit userEditDto(Integer userId) {
		TUserEntity entity = tUserRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("ユーザーが存在しません: userId=" + userId));
		DtoUserEdit dto = new DtoUserEdit();
		dto.setUserId(entity.getUserId());
		// 必要に応じて他のフィールドもセット
		return dto;
	}

	public TUserEntity finishUserEdit(DtoUserEdit dtuser) {
		TUserEntity editedUserInfo = tUserRepository.findById(dtuser.getUserIdAsIntger())
				.orElseThrow(() -> new RuntimeException("ユーザーが存在しません"));
		editedUserInfo.setName(dtuser.getName());
		editedUserInfo.setDepartmentId(dtuser.getDepartmentIdAsInteger());
		return tUserRepository.save(editedUserInfo);
	}
}