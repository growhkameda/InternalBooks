package com.example.internalbooks.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.internalbooks.entity.TUserEntity;

/**
 * MUserテーブルに対してどういった処理を行うかのリポジトリ
 * 現在はTUserRepositoryを使用しているが、本番環境にデプロイ前にMUserRepositoryに変更する場合は、このクラスを変更する必要がある
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

    /**
     * 削除フラグでTユーザを検索するメソッド
     * @param deleteFlg 0:有効ユーザー, 1:削除済みユーザー
     * @return 指定削除フラグのユーザーリスト
     */
    List<TUserEntity> findByDeleteFlg(Integer deleteFlg);
}
