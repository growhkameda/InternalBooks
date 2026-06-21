package com.example.internalbooks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

/**
 * LocalImageStorageService の単体テスト。
 *
 * 初学者向けメモ:
 * - {@link TempDir} はテスト実行ごとに使い捨ての一時ディレクトリを作ってくれる便利な仕組み。
 *   テスト終了後に自動で消えるため、ディスクにゴミが残らない。
 * - 実装は user.dir（プロジェクトルート）を起点にディレクトリを解決するため、
 *   テスト中だけ user.dir を一時ディレクトリへ差し替えている。
 */
class LocalImageStorageServiceTest {

    @TempDir
    Path tempDir;

    private LocalImageStorageService service;

    @BeforeEach
    void setUp() {
        // user.dir を一時ディレクトリに向ける（実装が user.dir を見るため）
        System.setProperty("user.dir", tempDir.toString());
        // imageDirectory を相対パスのサブディレクトリに設定
        service = new LocalImageStorageService("images");
    }

    // ─── savetbook ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("savetbook_正常_UUIDベースの一時ファイル名で保存しファイル名を返す")
    void savetbook_savesFileAndReturnsUuidFileName() throws IOException {
        // Arrange: png ファイルをアップロードする想定
        MockMultipartFile file = new MockMultipartFile(
            "file", "test-image.png", "image/png", "dummy content".getBytes());

        // Act
        String result = service.savetbook(file);

        // Assert: ファイル名は "tmp_xxx.png" 形式で実ファイルが作成されている
        assertThat(result).startsWith("tmp_").endsWith(".png");
        Path saved = tempDir.resolve("images").resolve(result);
        assertThat(Files.exists(saved)).isTrue();
    }

    @Test
    @DisplayName("savetbook_正常_既存書籍と同名アップロードでも既存画像を上書きしない")
    void savetbook_doesNotOverwriteExistingBookImage() throws IOException {
        // Arrange: 既存書籍の画像 10020009.png をあらかじめ配置
        Path imagesDir = tempDir.resolve("images");
        Files.createDirectories(imagesDir);
        Path existingImage = imagesDir.resolve("10020009.png");
        Files.writeString(existingImage, "existing book image");

        MockMultipartFile uploadedFile = new MockMultipartFile(
            "file", "10020009.png", "image/png", "new upload".getBytes());

        // Act
        String result = service.savetbook(uploadedFile);

        // Assert: 既存画像は破壊されておらず、新規ファイルは別名で保存されている
        assertThat(Files.readString(existingImage)).isEqualTo("existing book image");
        assertThat(result).startsWith("tmp_").endsWith(".png");
        assertThat(result).isNotEqualTo("10020009.png");
    }

    @Test
    @DisplayName("savetbook_境界_imagesディレクトリが未作成でも自動で作って保存する")
    void savetbook_createsDirectoryIfMissing() throws IOException {
        // Arrange: imagesディレクトリは存在しない（@TempDirは空状態）
        Path imagesDir = tempDir.resolve("images");
        assertThat(Files.exists(imagesDir)).isFalse();

        MockMultipartFile file = new MockMultipartFile(
            "file", "x.png", "image/png", "data".getBytes());

        // Act
        String result = service.savetbook(file);

        // Assert: ディレクトリが自動作成され、その中にファイルが保存されている
        assertThat(Files.exists(imagesDir)).isTrue();
        assertThat(Files.exists(imagesDir.resolve(result))).isTrue();
    }

    @Test
    @DisplayName("savetbook_境界_アップロードファイル名がpng以外でも保存ファイル名はpngになる")
    void savetbook_alwaysUsesPngExtension() throws IOException {
        // Arrange: 元ファイル名は jpg だが、実装は固定で .png を付ける仕様
        MockMultipartFile jpgFile = new MockMultipartFile(
            "file", "photo.jpg", "image/jpeg", "jpeg-bytes".getBytes());

        // Act
        String result = service.savetbook(jpgFile);

        // Assert
        assertThat(result).endsWith(".png");
    }

    // ─── deleteImage ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteImage_正常_ファイルが存在する場合は削除してtrueを返す")
    void deleteImage_existingFile_deletesAndReturnsTrue() throws Exception {
        // Arrange: 削除対象ファイルを配置
        Path imagesDir = tempDir.resolve("images");
        Files.createDirectories(imagesDir);
        Path imageFile = imagesDir.resolve("1.png");
        Files.writeString(imageFile, "dummy");

        // Act
        boolean result = service.deleteImage(1);

        // Assert: ファイルが消えている
        assertThat(result).isTrue();
        assertThat(Files.exists(imageFile)).isFalse();
    }

    @Test
    @DisplayName("deleteImage_境界_対象ファイルが存在しなくてもtrueを返す")
    void deleteImage_nonExistingFile_returnsTrue() throws Exception {
        // Arrange: 9999.png は存在しない

        // Act
        boolean result = service.deleteImage(9999);

        // Assert: 「もう無い」を異常扱いにしない
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("deleteImage_境界_bookIdがnullでもtrueを返してスキップする")
    void deleteImage_nullBookId_returnsTrue() throws Exception {
        // Arrange: bookId=null

        // Act
        boolean result = service.deleteImage(null);

        // Assert
        assertThat(result).isTrue();
    }

    // ─── renameToBookId ───────────────────────────────────────────────────────

    @Test
    @DisplayName("renameToBookId_正常_ファイルをbookId.pngへリネームする")
    void renameToBookId_renamesFile() throws IOException {
        // Arrange: 元ファイル "temp-upload.png" を配置
        Path imagesDir = tempDir.resolve("images");
        Files.createDirectories(imagesDir);
        Path source = imagesDir.resolve("temp-upload.png");
        Files.writeString(source, "image data");

        // Act
        service.renameToBookId("temp-upload.png", 42);

        // Assert: 新名のファイルが作られ、元名のファイルは消えている
        Path renamed = imagesDir.resolve("42.png");
        assertThat(Files.exists(renamed)).isTrue();
        assertThat(Files.exists(source)).isFalse();
    }

    @Test
    @DisplayName("renameToBookId_正常_既に同名bookId.pngが存在する場合は上書きする")
    void renameToBookId_overwritesExistingTarget() throws IOException {
        // Arrange: 元ファイルとリネーム先ファイルの両方を準備
        Path imagesDir = tempDir.resolve("images");
        Files.createDirectories(imagesDir);
        Path source = imagesDir.resolve("tmp_xxx.png");
        Files.writeString(source, "new content");
        Path existingTarget = imagesDir.resolve("100.png");
        Files.writeString(existingTarget, "old content");

        // Act
        service.renameToBookId("tmp_xxx.png", 100);

        // Assert: REPLACE_EXISTING で上書きされ、新内容に置き換わっている
        assertThat(Files.exists(source)).isFalse();
        assertThat(Files.readString(existingTarget)).isEqualTo("new content");
    }

    @Test
    @DisplayName("renameToBookId_異常_ソースファイルが存在しない場合はIOExceptionをスロー")
    void renameToBookId_sourceMissing_throwsIOException() {
        // Arrange: ファイルを配置しない

        // Act & Assert
        assertThatThrownBy(() -> service.renameToBookId("not-exists.png", 1))
            .isInstanceOf(IOException.class)
            .hasMessageContaining("リネーム元の画像ファイルが見つかりません");
    }

    @Test
    @DisplayName("renameToBookId_異常_currentFileNameがnullまたは空でIOExceptionをスロー")
    void renameToBookId_blankSource_throwsIOException() {
        // Act & Assert: null / 空文字 / 空白文字いずれもガード句で弾かれる
        assertThatThrownBy(() -> service.renameToBookId(null, 1))
            .isInstanceOf(IOException.class)
            .hasMessageContaining("リネーム元のファイル名が指定されていません");

        assertThatThrownBy(() -> service.renameToBookId("   ", 1))
            .isInstanceOf(IOException.class)
            .hasMessageContaining("リネーム元のファイル名が指定されていません");
    }

    @Test
    @DisplayName("renameToBookId_異常_bookIdがnullでIOExceptionをスロー")
    void renameToBookId_nullBookId_throwsIOException() {
        // Act & Assert: ガード句で弾かれる
        assertThatThrownBy(() -> service.renameToBookId("tmp_xxx.png", null))
            .isInstanceOf(IOException.class)
            .hasMessageContaining("bookId");
    }
}
