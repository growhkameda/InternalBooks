package com.example.internalbooks.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

class LocalImageStorageServiceTest {

    @TempDir
    Path tempDir;

    private LocalImageStorageService service;

    @BeforeEach
    void setUp() {
        // user.dir を一時ディレクトリに向ける
        System.setProperty("user.dir", tempDir.toString());
        // imageDirectory を相対パスのサブディレクトリに設定
        service = new LocalImageStorageService("images");
    }

    // ─── savetbook ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("savetbook_正常_UUIDベースの一時ファイル名で保存してファイル名を返す")
    void savetbook_savesFileAndReturnsUuidFileName() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test-image.png", "image/png", "dummy content".getBytes());

        String result = service.savetbook(file);

        assertThat(result).startsWith("tmp_").endsWith(".png");
        Path saved = tempDir.resolve("images").resolve(result);
        assertThat(Files.exists(saved)).isTrue();
    }

    @Test
    @DisplayName("savetbook_正常_既存書籍と同名ファイルを登録しても既存画像を上書きしない")
    void savetbook_doesNotOverwriteExistingBookImage() throws IOException {
        // 既存書籍の画像 10020009.png をあらかじめ配置
        Path imagesDir = tempDir.resolve("images");
        Files.createDirectories(imagesDir);
        Path existingImage = imagesDir.resolve("10020009.png");
        Files.writeString(existingImage, "existing book image");

        // 同名ファイルをアップロード
        MockMultipartFile file = new MockMultipartFile(
            "file", "10020009.png", "image/png", "new upload".getBytes());

        String result = service.savetbook(file);

        // 既存画像が破壊されていないこと
        assertThat(Files.readString(existingImage)).isEqualTo("existing book image");
        // 一時ファイルは別名で保存されていること
        assertThat(result).startsWith("tmp_").endsWith(".png");
        assertThat(result).isNotEqualTo("10020009.png");
    }

    // ─── deleteImage ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteImage_正常_ファイルが存在する場合削除してtrueを返す")
    void deleteImage_existingFile_deletesAndReturnsTrue() throws Exception {
        Path imagesDir = tempDir.resolve("images");
        Files.createDirectories(imagesDir);
        Path imageFile = imagesDir.resolve("1.png");
        Files.writeString(imageFile, "dummy");

        boolean result = service.deleteImage(1);

        assertThat(result).isTrue();
        assertThat(Files.exists(imageFile)).isFalse();
    }

    @Test
    @DisplayName("deleteImage_正常_ファイルが存在しなくてもtrueを返す")
    void deleteImage_nonExistingFile_returnsTrue() throws Exception {
        boolean result = service.deleteImage(9999);

        assertThat(result).isTrue();
    }

    // ─── renameToBookId ───────────────────────────────────────────────────────

    @Test
    @DisplayName("renameToBookId_正常_ファイルをbookId.pngにリネーム")
    void renameToBookId_renamesFile() throws IOException {
        Path imagesDir = tempDir.resolve("images");
        Files.createDirectories(imagesDir);
        Path source = imagesDir.resolve("temp-upload.png");
        Files.writeString(source, "image data");

        service.renameToBookId("temp-upload.png", 42);

        Path renamed = imagesDir.resolve("42.png");
        assertThat(Files.exists(renamed)).isTrue();
        assertThat(Files.exists(source)).isFalse();
    }
}
