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
 * ★将来的にm_bookテーブルに変更する場合は、このクラスを変更する必要がある★
 */
public interface TBookRepository extends JpaRepository<TBookEntity, Integer> {
	
	// 指定されたユーザーIDが借りている書籍を取得
	@Query("SELECT b FROM TBookEntity b WHERE b.borrowerId = :borrowerId")
	List<TBookEntity> findByBorrowerId(@Param("borrowerId") Integer borrowerId);
	
	// カテゴリーに一致する本情報を取得する
	List<TBookEntity> findByCategories(String category);
		
	// 書籍返却時にborrowerIdをNULLに更新する
	@Modifying
    @Transactional
    @Query("UPDATE TBookEntity b SET b.borrowerId = NULL WHERE b.bookId = :bookId")
    void clearBorrowerByBookId(@Param("bookId") Integer bookId);
	
	@Modifying
	@Transactional
	@Query("UPDATE TBookEntity b SET b.borrowerId = :userId WHERE b.id = :bookId")
	void updateBorrowerByBookId(@Param("bookId") Integer bookId, @Param("userId") Integer userId);

}