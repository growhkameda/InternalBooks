package com.example.internalbooks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.internalbooks.entity.TBookEntity;

/**
 * TBookテーブルに対してどういった処理を行うかのリポジトリ
 */
public interface TBookRepository extends JpaRepository<TBookEntity, Integer> {

    // カテゴリーに一致する本情報を取得する
    List<TBookEntity> findByCategories(String category);
}