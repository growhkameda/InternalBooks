package com.example.internalbooks.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 書籍画像を指定したディレクトリへ保存する。
     */
    public String savetbook(MultipartFile file) throws IOException;

    /**
     * 仮ファイル名で保存済みの画像を {bookId}.png にリネームする。
     * DB保存後に確定した bookId でファイルを正式名称に変更するために使用する。
     */
    void renameToBookId(String currentFileName, Integer bookId) throws IOException;

}
