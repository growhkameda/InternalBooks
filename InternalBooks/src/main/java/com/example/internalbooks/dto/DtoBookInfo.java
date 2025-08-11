package com.example.internalbooks.dto;

import lombok.Data;

/**
 * 書籍情報を格納するDTO
 * 貸出状況、詳細表示、検索結果など汎用的に使用
 */
@Data
public class DtoBookInfo {
    
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
    
    // デフォルトコンストラクタ
    public DtoBookInfo() {
        // デフォルト値は設定せず、使用時に適切な値を設定する
    }
    
    // 書籍IDに基づいて画像URLを設定するメソッド
    public void setImageUrlFromBookId() {
        if (this.bookId != null) {
            // 書籍IDは4桁でカテゴリーごとに分けられる設計
            String imageName = this.bookId + ".png";
            this.imageUrl = "/images/" + imageName;
        } else {
            // デフォルト画像を設定
            this.imageUrl = "/images/default-book.png";
        }
    }
}