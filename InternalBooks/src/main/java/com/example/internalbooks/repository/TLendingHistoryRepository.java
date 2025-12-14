package com.example.internalbooks.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.internalbooks.entity.TLendingHistoryEntity;

@Repository
public interface TLendingHistoryRepository extends JpaRepository<TLendingHistoryEntity, Integer> {

    // 書籍ごとの履歴（最新の履歴を最初に取得）
    @Query("SELECT t FROM TLendingHistoryEntity t WHERE t.bookId = :bookId ORDER BY t.lendingDate DESC")
    List<TLendingHistoryEntity> findByBookId(@Param("bookId") Integer bookId);

    // ユーザーごとの履歴
    List<TLendingHistoryEntity> findByUserId(Integer userId);
    
    // bookId と status で検索するメソッド
//    Optional<TLendingHistoryEntity> findByBookIdAndReturnDateIsNull(Integer bookId);
//    Optional<TLendingHistoryEntity> findByBookIdAndUserId(Integer bookId, Integer userId);
//    Optional<TLendingHistoryEntity> findByBookIdAndStatus(Integer bookId, String status);
    
    @Query("SELECT t FROM TLendingHistoryEntity t " +
    	       "WHERE t.bookId = :bookId " +
    	       "AND t.userId = :userId " +
    	       "AND (t.returnDate IS NULL OR t.review IS NULL)")
    	Optional<TLendingHistoryEntity> findActiveLendingHistory(
    	        @Param("bookId") Integer bookId,
    	        @Param("userId") Integer userId);


    // 必要なら複数件返すバージョン
//    List<TLendingHistoryEntity> findAllByBookIdAndStatus(Integer bookId, String status);

    /** 11/03 木俣
     * 指定されたbook_idの貸出履歴をすべて削除
     * 書籍削除時のカスケード削除に使用
     */
    @Modifying
    @Transactional
    void deleteByBookId(Integer bookId);
    
}
