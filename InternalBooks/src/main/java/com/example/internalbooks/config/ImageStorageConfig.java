package com.example.internalbooks.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.example.internalbooks.service.ImageStorageService;
import com.example.internalbooks.service.LocalImageStorageService;
import com.example.internalbooks.service.S3ImageStorageService;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * 画像ストレージサービスの設定クラス
 * 環境に応じて適切なImageStorageService実装をBean登録する
 */
@Configuration
public class ImageStorageConfig {

    @Value("${app.image.storage.s3.region:ap-northeast-1}")
    private String s3Region;

    @Value("${app.image.storage.local.path:src/main/resources/static/images}")
    private String localImagePath;

    @Value("${app.image.storage.s3.bucket-name:}")
    private String s3BucketName;

    @Value("${app.image.storage.s3.prefix:}")
    private String s3Prefix;

    /**
     * ローカル環境用のImageStorageService Bean
     * app.image.storage.type=local の場合に有効
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = "app.image.storage.type", havingValue = "local", matchIfMissing = true)
    public ImageStorageService localImageStorageService() {
        return new LocalImageStorageService(localImagePath);
    }

    /**
     * AWS S3環境用のImageStorageService Bean
     * app.image.storage.type=s3 の場合に有効
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = "app.image.storage.type", havingValue = "s3")
    public ImageStorageService s3ImageStorageService(S3Client s3Client) {
        return new S3ImageStorageService(s3Client, s3BucketName, s3Prefix);
    }

    /**
     * AWS S3クライアントのBean
     * S3ImageStorageServiceが使用される場合のみ必要
     */
    @Bean
    @ConditionalOnProperty(name = "app.image.storage.type", havingValue = "s3")
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(s3Region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}

