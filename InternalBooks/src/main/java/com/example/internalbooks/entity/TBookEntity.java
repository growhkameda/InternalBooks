package com.example.internalbooks.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.example.internalbooks.common.Const;

import lombok.Data;

@Data
@Entity
@Table(name = "t_book")
/**
 * TBookテーブルからデータを受け取るためのEntity
 */
public class TBookEntity {
    @Id
    @Column(name = Const.BOOK_ID)
    private Integer bookId;
    
    @Column(name = Const.TITLE)
    private String title;
    
    @Column(name = Const.CATEGORIES)
    private String categories;
    
    @Column(name = Const.BORROWER_ID)
    private Integer borrowerId;
    
    @Column(name = Const.PROVIDER_ID)
    private String providerId;
    
    @Column(name = Const.PROVIDER_COMMENT)
    private String providerComment;

    @Column(name = Const.MEMO)
    private String memo;
}
