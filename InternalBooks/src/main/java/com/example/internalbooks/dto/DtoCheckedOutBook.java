package com.example.internalbooks.dto;

import lombok.Data;

/**
 * 貸出中書籍情報を格納するDTO
 */
@Data
public class DtoCheckedOutBook {
    
    // 書籍ID
    private Integer bookId;
    
    // 書籍タイトル
    private String title;
    
    // カテゴリー
    private String category;
    
    // 貸出状況
    private String status;
    
    // 書籍画像URL
    private String imageUrl;
    
    //コンストラクタ
    public DtoCheckedOutBook() {
        this.status = "貸出中";
    }
    
    // 書籍IDに基づいて画像URLを設定するメソッド
    public void setImageUrlFromBookId() {
        if (this.bookId != null) {
            // 書籍IDは4桁でカテゴリーごとに分けられる設計（1xxx, 2xxx, ...）
            String imageName = this.bookId + ".png";
            this.imageUrl = "/images/" + imageName;
        } else {
            this.imageUrl = "/images/default-book.png";
        }
    }
}