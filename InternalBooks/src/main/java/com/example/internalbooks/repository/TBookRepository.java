package com.example.internalbooks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.internalbooks.entity.TBookEntity;


/**
 * t_bookテーブルに対する処理を行うリポジトリ
 * ★将来的にm_bookテーブルに変更する場合は、このクラスを変更する必要がある★
 */
public interface TBookRepository extends JpaRepository<TBookEntity, Integer> {
	
	// 指定されたユーザーIDが借りている書籍を取得
	@Query("SELECT b FROM TBookEntity b WHERE b.borrowerId = :borrowerId")
	List<TBookEntity> findByBorrowerId(@Param("borrowerId") Integer borrowerId);
		
}