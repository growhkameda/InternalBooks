package com.example.internalbooks.service;

/**
 * 画像ストレージサービスのインターフェース
 * ローカルファイルシステムまたはAWS S3など、異なるストレージ実装を抽象化
 */
public interface ImageStorageService {
    
    /**
     * 指定されたbookIdに対応する画像を削除する
     * 削除が成功した場合true、画像が存在しない場合もtrueを返す
     */
    boolean deleteImage(Integer bookId) throws Exception;
}

