package com.example.internalbooks.service;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * AWS S3用の画像ストレージサービス実装
 * S3バケットから {bookId}.png を削除する
 * ImageStorageConfigでBean登録されるため、@Serviceアノテーションは不要
 */
public class S3ImageStorageService implements ImageStorageService {

    private static final Logger logger = LoggerFactory.getLogger(S3ImageStorageService.class);

    private final S3Client s3Client;
    private final String bucketName;
    private final String prefix; // オプション: 画像のプレフィックスパス（例: "images/"）

    public S3ImageStorageService(S3Client s3Client, String bucketName, String prefix) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.prefix = prefix != null ? prefix : "";
    }

    /**
     * 指定されたbookIdに対応する画像を削除する(AWS S3用)
     * 削除が成功した場合trueを返す
     * 画像が存在しない場合もtrueを返す
     * 削除が失敗したときのみfalseを返すが、DBの削除は継続する
     */
    @Override
    public boolean deleteImage(Integer bookId) throws Exception {
        if (bookId == null) {
            logger.warn("bookIdがnullのため、画像削除をスキップします");
            return true;
        }

        if (bucketName == null || bucketName.isEmpty()) {
            logger.error("S3バケット名が設定されていません");
            return false;
        }

        try {
            // S3オブジェクトキーを構築
            String objectKey = prefix + bookId + ".png";

            // 削除リクエストを作成
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            // S3からオブジェクトを削除
            s3Client.deleteObject(deleteRequest);
            logger.info("S3から画像を削除しました: bucket={}, key={}", bucketName, objectKey);
            return true;

        } catch (NoSuchKeyException e) {
            // オブジェクトが存在しない場合は成功として扱う
            logger.warn("S3に画像が存在しません: bookId={}, error={}", bookId, e.getMessage());
            return true;

        } catch (SdkException e) {
            logger.error("S3からの画像削除に失敗しました: bookId={}, error={}", bookId, e.getMessage(), e);
            // エラーが発生しても例外をスローせず、falseを返す
            // DB削除は継続するため、例外はスローしない
            return false;
        }
    }

    // S3用書籍画像登録（一時キーに保存し、renameToBookIdで確定）
    @Override
    public String savetbook(MultipartFile file) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String tmpKey = prefix + "tmp/" + uuid + ".png";

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(tmpKey)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()));
        } catch (SdkException e) {
            throw new IOException("S3への画像アップロードに失敗しました: " + e.getMessage(), e);
        }

        return tmpKey;
    }

    /**
     * S3上の仮ファイル名オブジェクトを {bookId}.png にコピー後、元オブジェクトを削除する。
     */
    @Override
    public void renameToBookId(String currentFileName, Integer bookId) throws IOException {
        if (currentFileName == null || currentFileName.isBlank()) {
            throw new IOException("リネーム元のファイル名が指定されていません");
        }
        if (bookId == null) {
            throw new IOException("リネーム先の bookId が指定されていません");
        }

        String sourceKey = currentFileName;
        String targetKey = prefix + bookId + ".png";

        try {
            CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                    .sourceBucket(bucketName)
                    .sourceKey(sourceKey)
                    .destinationBucket(bucketName)
                    .destinationKey(targetKey)
                    .build();
            s3Client.copyObject(copyRequest);

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(sourceKey)
                    .build();
            s3Client.deleteObject(deleteRequest);

            logger.info("S3上の画像をリネームしました: {} → {}", sourceKey, targetKey);

        } catch (SdkException e) {
            throw new IOException("S3上の画像リネームに失敗しました: " + e.getMessage(), e);
        }
    }

}
