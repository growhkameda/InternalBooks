package com.example.internalbooks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.internalbooks.entity.TLendingHistoryEntity;

@Repository
public interface TLendingHistoryRepository extends JpaRepository<TLendingHistoryEntity, Integer> {

    // 書籍ごとの履歴
    List<TLendingHistoryEntity> findByBookId(Integer bookId);

    // ユーザーごとの履歴
    List<TLendingHistoryEntity> findByUserId(Integer userId);
    
    // bookId と status で検索するメソッド
//    Optional<TLendingHistoryEntity> findByBookIdAndStatus(Integer bookId, String status);

    // 必要なら複数件返すバージョン
//    List<TLendingHistoryEntity> findAllByBookIdAndStatus(Integer bookId, String status);
}
