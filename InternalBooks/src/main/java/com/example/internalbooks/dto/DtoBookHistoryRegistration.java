package com.example.internalbooks.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DtoBookHistoryRegistration {
	
	private Integer id;
	
	// 書籍ID
	private Integer bookId;
	
	// 貸出予定日
	private LocalDateTime lendingDate;
	
	// 返却日
	private LocalDateTime scheduledReturnDate;
	
	// 返却予定日
	private LocalDateTime returnDate;
	
	// ユーザーID
	private Integer userId;
	
	// レビュー
	private String review;
}
