package com.example.internalbooks.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.internalbooks.entity.TUserEntity;


/**
 * MUserテーブルに対してどういった処理を行うかのリポジトリ
 */
public interface TUserRepository extends JpaRepository<TUserEntity, Integer> {
	
	/**
	 * メールアドレスからTユーザの情報を取得するメソッド
	 * @param mailAddress メールアドレス
	 * @return Tユーザ情報
	 */
	Optional<TUserEntity> findByMailAddress(String mailAddress);
	
	/**
	 * idからTユーザの情報を取得するメソッド
	 * @param id ユーザのID
	 * @return Tユーザ情報
	 */
	Optional<TUserEntity> findById(Integer id);
	
}