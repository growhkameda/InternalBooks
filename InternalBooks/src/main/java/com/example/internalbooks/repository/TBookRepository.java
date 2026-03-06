package com.example.internalbooks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.internalbooks.entity.TBookEntity;

/**
 * t_bookテーブルに対する処理を行うリポジトリ
 */
public interface TBookRepository extends JpaRepository<TBookEntity, Integer> {

	// 指定されたユーザーIDが借りている書籍を取得
	@Query("SELECT b FROM TBookEntity b WHERE b.borrowerId = :borrowerId")
	List<TBookEntity> findByBorrowerId(@Param("borrowerId") Integer borrowerId);

	// カテゴリーに一致する本情報を取得する
	List<TBookEntity> findByCategories(String category);

	// カテゴリ名よりIDを取得 大文字小文字関係なし
	@Query("SELECT MAX(b.bookId) FROM TBookEntity b WHERE LOWER(b.categories) = LOWER(:categories)")
	Integer findMaxIdByName(@Param("categories") String categories);

	// IDの最大値を取得する。
	@Query("SELECT MAX(b.bookId) FROM TBookEntity b")
	Integer findMaxBookId();

	// 書籍返却時にborrowerIdをNULLに更新する
	@Modifying
	@Transactional
	@Query("UPDATE TBookEntity b SET b.borrowerId = NULL WHERE b.bookId = :bookId")
	void clearBorrowerByBookId(@Param("bookId") Integer bookId);

	@Modifying
	@Transactional
	@Query("UPDATE TBookEntity b SET b.borrowerId = :userId WHERE b.bookId = :bookId")
	void updateBorrowerByBookId(@Param("bookId") Integer bookId, @Param("userId") Integer userId);

}