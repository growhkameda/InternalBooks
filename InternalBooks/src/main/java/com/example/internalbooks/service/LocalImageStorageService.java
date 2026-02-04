package com.example.internalbooks.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * ローカルファイルシステム用の画像ストレージサービス実装
 * src/main/resources/static/images/{bookId}.png を削除する
 * ImageStorageConfigでBean登録されるため、@Serviceアノテーションは不要
 */
public class LocalImageStorageService implements ImageStorageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalImageStorageService.class);

    private final String imageDirectory;

    public LocalImageStorageService(String imageDirectory) {
        this.imageDirectory = imageDirectory;
    }

    /**
     * 指定されたbookIdに対応する画像を削除する
     * 削除が存在しない場合もtrueを返す
     * 削除が失敗したときのみfalseを返すが、DBの削除は継続する
     */
    @Override
    public boolean deleteImage(Integer bookId) throws Exception {
        if (bookId == null) {
            logger.warn("bookIdがnullのため、画像削除をスキップします");
            return true;
        }

        try {
            // 画像ファイルのパスを構築
            String fileName = bookId + ".png";
            Path imagePath = Paths.get(imageDirectory, fileName);

            // ファイルが存在するか確認
            if (!Files.exists(imagePath)) {
                logger.warn("画像ファイルが存在しません: {}", imagePath);
                return true; // 存在しない場合は成功として扱う
            }

            // ファイルを削除
            Files.delete(imagePath);
            logger.info("画像ファイルを削除しました: {}", imagePath);
            return true;

        } catch (IOException e) {
            logger.error("画像ファイルの削除に失敗しました: bookId={}, error={}", bookId, e.getMessage(), e);
            // エラーが発生しても例外をスローせず、falseを返す
            // DB削除は継続するため、例外はスローしない
            return false;
        }
    }
    
    /**
     * 書籍画像を指定したディレクトリへ保存する。
     */
    @Override
    public String savetbook(MultipartFile file) throws IOException {
    	
    	// 画像の名前を変数を保存
    	String fileName = file.getOriginalFilename();
    	// 絶対パスの指定
    	String projectDir = System.getProperty("user.dir");
    	String path = projectDir + imageDirectory;
    	File saveFile = new File(path, fileName);
    	//saveFileに格納されたURLにアップロードされた画像を物理的に書き込む
    	file.transferTo(saveFile);
    	//URLを返す
    	return fileName;
    }
    
}

