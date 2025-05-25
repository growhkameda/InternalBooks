package com.example.internalbooks.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.internalbooks.entity.MUserEntity;


/**
 * MUserテーブルに対してどういった処理を行うかのリポジトリ
 */
public interface MUserRepository extends JpaRepository<MUserEntity, Integer> {
	
	/**
	 * メールアドレスからMユーザの情報を取得するメソッド
	 * @param email メールアドレス
	 * @return Mユーザ情報
	 */
	Optional<MUserEntity> findByEmail(String email);
	
	/**
	 * idからMユーザの情報を取得するメソッド
	 * @param id ユーザのID
	 * @return Mユーザ情報
	 */
	Optional<MUserEntity> findById(Integer id);
	
}