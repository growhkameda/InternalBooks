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
    @DisplayName("savetbook_正常_ファイルを保存してファイル名を返す")
    void savetbook_savesFileAndReturnsFileName() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test-image.png", "image/png", "dummy content".getBytes());

        String result = service.savetbook(file);

        assertThat(result).isEqualTo("test-image.png");
        Path saved = tempDir.resolve("images").resolve("test-image.png");
        assertThat(Files.exists(saved)).isTrue();
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
