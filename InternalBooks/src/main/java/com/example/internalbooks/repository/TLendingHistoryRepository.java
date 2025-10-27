package com.example.internalbooks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.internalbooks.entity.TLendingHistoryEntity;

@Repository
public interface TLendingHistoryRepository extends JpaRepository<TLendingHistoryEntity, Integer> {

    // 書籍ごとの履歴（最新の履歴を最初に取得）
    @Query("SELECT t FROM TLendingHistoryEntity t WHERE t.bookId = :bookId ORDER BY t.lendingDate DESC")
    List<TLendingHistoryEntity> findByBookId(@Param("bookId") Integer bookId);

    // ユーザーごとの履歴
    List<TLendingHistoryEntity> findByUserId(Integer userId);
    
    // bookId と status で検索するメソッド
//    Optional<TLendingHistoryEntity> findByBookIdAndStatus(Integer bookId, String status);

    // 必要なら複数件返すバージョン
//    List<TLendingHistoryEntity> findAllByBookIdAndStatus(Integer bookId, String status);
}
