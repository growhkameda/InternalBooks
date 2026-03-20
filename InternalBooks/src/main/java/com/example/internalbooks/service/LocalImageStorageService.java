package com.example.internalbooks.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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
     * 画像保存ディレクトリの絶対パスを返す。
     * user.dir（プロジェクトルート）を基点に imageDirectory を結合する。
     * Java の Path API がOSのファイルセパレータを自動処理するため Mac/Windows 両対応。
     */
    private Path resolveImageDir() {
        return Paths.get(System.getProperty("user.dir")).resolve(imageDirectory).normalize();
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
            String fileName = bookId + ".png";
            Path imagePath = resolveImageDir().resolve(fileName);

            if (!Files.exists(imagePath)) {
                logger.warn("画像ファイルが存在しません: {}", imagePath);
                return true;
            }

            Files.delete(imagePath);
            logger.info("画像ファイルを削除しました: {}", imagePath);
            return true;

        } catch (IOException e) {
            logger.error("画像ファイルの削除に失敗しました: bookId={}, error={}", bookId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 書籍画像を指定したディレクトリへ一時保存する。
     * 既存画像の上書きを防ぐため UUID ベースの一時ファイル名を使用する。
     * DB登録後に renameToBookId() で正式名称 ({bookId}.png) にリネームすること。
     */
    @Override
    public String savetbook(MultipartFile file) throws IOException {

        String tmpFileName = "tmp_" + UUID.randomUUID().toString() + ".png";

        Path saveDirPath = resolveImageDir();

        if (!Files.exists(saveDirPath)) {
            Files.createDirectories(saveDirPath);
            logger.info("画像保存ディレクトリを作成しました: {}", saveDirPath);
        }

        Path saveFilePath = saveDirPath.resolve(tmpFileName).toAbsolutePath();
        file.transferTo(saveFilePath);
        logger.info("画像ファイルを一時保存しました: {}", saveFilePath);

        return tmpFileName;
    }

    /**
     * 仮ファイル名で保存済みの画像を {bookId}.png にリネームする。
     * DB保存後に確定した bookId でファイルを正式名称に変更するために使用する。
     */
    @Override
    public void renameToBookId(String currentFileName, Integer bookId) throws IOException {
        if (currentFileName == null || currentFileName.isBlank()) {
            throw new IOException("リネーム元のファイル名が指定されていません");
        }
        if (bookId == null) {
            throw new IOException("リネーム先の bookId が指定されていません");
        }

        Path imageDir = resolveImageDir();
        Path sourcePath = imageDir.resolve(currentFileName).toAbsolutePath();
        Path targetPath = imageDir.resolve(bookId + ".png").toAbsolutePath();

        if (!Files.exists(sourcePath)) {
            throw new IOException("リネーム元の画像ファイルが見つかりません: " + sourcePath);
        }

        Files.move(sourcePath, targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        logger.info("画像ファイルをリネームしました: {} → {}", sourcePath, targetPath);
    }

}
