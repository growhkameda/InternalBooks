package com.example.internalbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.internalbooks.entity.TBookEntity;


/**
 * TBookテーブルに対してどういった処理を行うかのリポジトリ
 */
public interface TBookRepository extends JpaRepository<TBookEntity, Integer> {
		
}