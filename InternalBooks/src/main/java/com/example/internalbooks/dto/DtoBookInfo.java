package com.example.internalbooks.dto;

import java.util.List;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 書籍情報を格納するDTO
 * 貸出状況、詳細表示、検索結果など汎用的に使用
 */
@Data
public class DtoBookInfo {
    
    // 書籍ID
    private Integer bookId= 1;
    
    // 書籍タイトル
    @NotBlank(message = "書籍名は必須です")
    private String title;
    
    // カテゴリー
    @NotBlank(message = "カテゴリーは必須です")
    private String category;
    
    private List<String> categories;
    
    // 貸出状況
    private String status;
    
    // 返却 感想・コメント
    private String memo;
    
    // 書籍画像URL
    // カテゴリー
    @NotBlank(message = "書籍画像は必須です。")
    private String imageUrl;
    
    // 書籍提供者
    @NotNull(message = "書籍提供者は必須です")
    private Integer providerId;
    
    // 書籍提供者のコメント
    @NotBlank(message = "コメントは必須です")
    private String providerComment;
    
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
    
    // getter/setter for categories
    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}