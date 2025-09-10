package com.example.internalbooks.dto;

import lombok.Data;

/**
 * 書籍履歴情報を格納するDTO
 */
@Data
public class DtoBookHistory {
    /**
     * ここにフィールドなど記述する感じになると思います(木俣)
     */
	
//	private Integer id;
	
	// 書籍ID
	private Integer book_Id;
    
	// 貸出日
	private String lendingDate;
    
	// 返却予定日
	private String scheduledReturnDate;
    
	// 実際の返却日
	private String returnDate;
	
	// 借りたユーザーの名前
	private String userName;
    
	// レビュー
	private String review;
    
}
