package com.example.internalbooks.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.internalbooks.service.ImageStorageService;
import com.example.internalbooks.service.LocalImageStorageService;

/**
 * 画像ストレージサービスの設定クラス
 * EBSマウントされたローカルファイルシステムを使用する
 */
@Configuration
public class ImageStorageConfig {

	@Value("${app.image.storage.local.directory:src/main/resources/static/images/}")
	private String localImageDirectory;

	@Bean
	public ImageStorageService localImageStorageService() {
		return new LocalImageStorageService(localImageDirectory);
	}
}
