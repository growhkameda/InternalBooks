package com.example.internalbooks.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.example.internalbooks.common.Const;

import lombok.Data;

@Data
@Entity
@Table(name = "t_lendinghistory")
/**
 * TLendingHistoryテーブルからデータを受け取るためのEntity
 */
public class TLendingHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Const.ID)
    private Integer id;
    
    @Column(name = Const.BOOK_ID)
    private Integer bookId;
    
    @Column(name = Const.LENDING_DATE)
    private LocalDateTime lendingDate;
    
    @Column(name = Const.SCHEDULED_RETURN_DATE)
    private LocalDateTime scheduledReturnDate;
    
    @Column(name = Const.RETURN_DATE)
    private LocalDateTime returnDate;
    
    @Column(name = Const.USER_ID)
    private Integer userId;

    @Column(name = Const.REVIEW)
    private String review;
}
