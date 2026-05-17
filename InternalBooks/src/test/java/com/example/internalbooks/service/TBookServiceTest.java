package com.example.internalbooks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.internalbooks.common.Const;
import com.example.internalbooks.dto.DtoBookInfo;
import com.example.internalbooks.entity.TBookEntity;
import com.example.internalbooks.entity.TLendingHistoryEntity;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.repository.TBookRepository;
import com.example.internalbooks.repository.TLendingHistoryRepository;
import com.example.internalbooks.repository.TUserRepository;

/**
 * TBookService の単体テスト。
 *
 * 初学者向けメモ:
 * - 「正常系」と「異常系」の両方を、メソッドごとに小さなテストへ分けて書く。
 * - 1つのテストで複数の挙動を一度に確認しないこと（失敗原因が特定しづらくなる）。
 * - DB やファイルシステムには直接触らず、すべてモックで代替する。
 */
@ExtendWith(MockitoExtension.class)
class TBookServiceTest {

    @Mock
    private TBookRepository tBookRepository;

    @Mock
    private TLendingHistoryRepository lendingHistoryRepository;

    @Mock
    private TUserRepository tUserRepository;

    @Mock
    private TUserService tUserService;

    @Mock
    private ImageStorageService imageStorageService;

    @InjectMocks
    private TBookService tBookService;

    // ─── getAllCategories ─────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllCategories_正常_カンマ区切りカテゴリを一意に展開する")
    void getAllCategories_deduplicatesCategories() {
        // Arrange: 2冊の本に重複するカテゴリが含まれている状態
        TBookEntity book1 = buildBook(1, "A,B", null);
        TBookEntity book2 = buildBook(2, "B,C", null);
        when(tBookRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(book1, book2));

        // Act
        List<String> result = tBookService.getAllCategories();

        // Assert: 重複は除去され、出現順で返る
        assertThat(result).containsExactly("A", "B", "C");
    }

    @Test
    @DisplayName("getAllCategories_境界_書籍0件のとき空リストを返す")
    void getAllCategories_emptyBookList_returnsEmptyList() {
        // Arrange: 書籍が1冊も無い状態
        when(tBookRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());

        // Act
        List<String> result = tBookService.getAllCategories();

        // Assert
        assertThat(result).isEmpty();
    }

    // ─── getPagedCategories ───────────────────────────────────────────────────

    @Test
    @DisplayName("getPagedCategories_正常_指定ページ範囲のカテゴリを返す")
    void getPagedCategories_returnsCorrectPage() {
        // Arrange: 全部で5カテゴリ（A〜E）が存在する状態
        TBookEntity book = buildBook(1, "A,B,C,D,E", null);
        when(tBookRepository.findAll(any(Sort.class))).thenReturn(Collections.singletonList(book));

        // Act: 0ページ目を 2件ずつ取得
        List<String> result = tBookService.getPagedCategories(0, 2);

        // Assert
        assertThat(result).containsExactly("A", "B");
    }

    @Test
    @DisplayName("getPagedCategories_境界_範囲外ページで空リストを返す")
    void getPagedCategories_outOfRangePage_returnsEmptyList() {
        // Arrange: 5カテゴリしかない
        TBookEntity book = buildBook(1, "A,B,C,D,E", null);
        when(tBookRepository.findAll(any(Sort.class))).thenReturn(Collections.singletonList(book));

        // Act: 10ページ目を 2件ずつ → fromIndex >= size で空リスト
        List<String> result = tBookService.getPagedCategories(10, 2);

        // Assert
        assertThat(result).isEmpty();
    }

    // ─── getCategoryTotalPages ────────────────────────────────────────────────

    @Test
    @DisplayName("getCategoryTotalPages_正常_総ページ数を切り上げで計算する")
    void getCategoryTotalPages_calculatesCorrectly() {
        // Arrange: カテゴリが5件
        TBookEntity book = buildBook(1, "A,B,C,D,E", null);
        when(tBookRepository.findAll(any(Sort.class))).thenReturn(Collections.singletonList(book));

        // Act: ページサイズ 2 → ceil(5/2) = 3
        int result = tBookService.getCategoryTotalPages(2);

        // Assert
        assertThat(result).isEqualTo(3);
    }

    @Test
    @DisplayName("getCategoryTotalPages_境界_カテゴリ0件で総ページ数0")
    void getCategoryTotalPages_emptyCategories_returnsZero() {
        // Arrange
        when(tBookRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());

        // Act
        int result = tBookService.getCategoryTotalPages(2);

        // Assert
        assertThat(result).isZero();
    }

    // ─── getBookById ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("getBookById_正常_書籍情報をDTOへ変換する")
    void getBookById_convertsEntityToDto() {
        // Arrange
        final Integer existingBookId = 100;
        TBookEntity book = buildBook(existingBookId, "Tech,Java", null);
        book.setTitle("Spring Book");
        when(tBookRepository.findById(existingBookId)).thenReturn(Optional.of(book));
        when(lendingHistoryRepository.findByBookId(existingBookId)).thenReturn(Collections.emptyList());

        // Act
        DtoBookInfo result = tBookService.getBookById(existingBookId);

        // Assert: 主要フィールドが反映されている
        assertThat(result).isNotNull();
        assertThat(result.getBookId()).isEqualTo(existingBookId);
        assertThat(result.getTitle()).isEqualTo("Spring Book");
        assertThat(result.getCategories()).containsExactly("Tech", "Java");
    }

    @Test
    @DisplayName("getBookById_正常_borrowerIdが設定されていれば貸出中と判定")
    void getBookById_lendingStatus_whenBorrowerIdExists() {
        // Arrange: borrowerId=5（誰かに貸出中）
        TBookEntity book = buildBook(100, "Tech", 5);
        book.setTitle("Borrowed Book");
        when(tBookRepository.findById(100)).thenReturn(Optional.of(book));

        // Act
        DtoBookInfo result = tBookService.getBookById(100);

        // Assert
        assertThat(result.getStatus()).isEqualTo("貸出中");
    }

    @Test
    @DisplayName("getBookById_正常_borrowerIdなし＆履歴なしで貸出可能と判定")
    void getBookById_lendingStatus_whenBorrowerIdIsNull() {
        // Arrange: 借りている人がおらず、貸出履歴も空
        TBookEntity book = buildBook(100, "Tech", null);
        book.setTitle("Available Book");
        when(tBookRepository.findById(100)).thenReturn(Optional.of(book));
        when(lendingHistoryRepository.findByBookId(100)).thenReturn(Collections.emptyList());

        // Act
        DtoBookInfo result = tBookService.getBookById(100);

        // Assert
        assertThat(result.getStatus()).isEqualTo("貸出可能");
    }

    @Test
    @DisplayName("getBookById_境界_borrowerIdなしでも未返却履歴があれば貸出中と判定")
    void getBookById_lendingStatus_whenUnreturnedHistoryExists() {
        // Arrange: borrowerId は null だが、履歴に未返却(returnDate==null)が残っているケース
        // データ不整合への対抗策として、履歴ベースでも「貸出中」になる隠れ分岐を検証する
        TBookEntity book = buildBook(100, "Tech", null);
        book.setTitle("Inconsistent Book");
        TLendingHistoryEntity unreturnedHistory = new TLendingHistoryEntity();
        unreturnedHistory.setReturnDate(null);

        when(tBookRepository.findById(100)).thenReturn(Optional.of(book));
        when(lendingHistoryRepository.findByBookId(100))
            .thenReturn(Collections.singletonList(unreturnedHistory));

        // Act
        DtoBookInfo result = tBookService.getBookById(100);

        // Assert
        assertThat(result.getStatus()).isEqualTo("貸出中");
    }

    @Test
    @DisplayName("getBookById_異常_書籍未存在でnullを返す")
    void getBookById_notFound_returnsNull() {
        // Arrange
        final Integer missingBookId = 999;
        when(tBookRepository.findById(missingBookId)).thenReturn(Optional.empty());

        // Act
        DtoBookInfo result = tBookService.getBookById(missingBookId);

        // Assert: 例外ではなく null が返る仕様
        assertThat(result).isNull();
    }

    // ─── getBooksByCategoryWithDetails ────────────────────────────────────────

    @Test
    @DisplayName("getBooksByCategoryWithDetails_正常_指定カテゴリを含む書籍だけDTOで返す")
    void getBooksByCategoryWithDetails_filtersByCategory() {
        // Arrange: 3冊のうち "Java" を含む本は 2冊
        TBookEntity book1 = buildBook(1, "Tech,Java", null);
        book1.setTitle("Java本");
        TBookEntity book2 = buildBook(2, "Design", null);
        book2.setTitle("デザイン本");
        TBookEntity book3 = buildBook(3, "Java,Spring", null);
        book3.setTitle("Spring本");

        when(tBookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));
        // book1 / book3 は Java カテゴリヒット時に貸出履歴を参照される（borrowerId=null のため）
        when(lendingHistoryRepository.findByBookId(1)).thenReturn(Collections.emptyList());
        when(lendingHistoryRepository.findByBookId(3)).thenReturn(Collections.emptyList());

        // Act
        List<DtoBookInfo> result = tBookService.getBooksByCategoryWithDetails("Java");

        // Assert: Java を含む 2冊だけが返り、同じ書籍は1回しか含まれない
        assertThat(result).hasSize(2);
        assertThat(result).extracting(DtoBookInfo::getBookId).containsExactly(1, 3);
    }

    // ─── getCheckedOutBooksByUserId ───────────────────────────────────────────

    @Test
    @DisplayName("getCheckedOutBooksByUserId_正常_借りている書籍をDTOで返す（返却予定日含む）")
    void getCheckedOutBooksByUserId_returnsBorrowedBooksWithDueDate() {
        // Arrange: ユーザー5が借りている書籍が1冊あり、返却予定日が登録されている
        final Integer borrowerUserId = 5;
        TBookEntity borrowedBook = buildBook(100, "Tech", borrowerUserId);
        borrowedBook.setTitle("Borrowed");

        TLendingHistoryEntity activeHistory = new TLendingHistoryEntity();
        activeHistory.setScheduledReturnDate(LocalDateTime.of(2026, 5, 1, 0, 0));

        when(tBookRepository.findByBorrowerId(borrowerUserId))
            .thenReturn(Collections.singletonList(borrowedBook));
        when(lendingHistoryRepository.findByBookId(100))
            .thenReturn(Collections.singletonList(activeHistory));

        // Act
        List<DtoBookInfo> result = tBookService.getCheckedOutBooksByUserId(borrowerUserId);

        // Assert: 借りている書籍が DTO 化され、返却予定日（日本語フォーマット）が入っている
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookId()).isEqualTo(100);
        assertThat(result.get(0).getStatus()).isEqualTo("貸出中");
        assertThat(result.get(0).getScheduledReturnDate()).contains("2026年05月01日");
    }

    // ─── processBookSearchRequest ─────────────────────────────────────────────

    @Test
    @DisplayName("processBookSearchRequest_正常_bookIdParamを優先して書籍を取得する")
    void processBookSearchRequest_prefersBookIdParam() {
        // Arrange: bookIdParam=42 / qrData="999" の両方が指定されたケース
        TBookEntity book = buildBook(42, "Tech", null);
        book.setTitle("Direct Lookup");
        when(tBookRepository.findById(42)).thenReturn(Optional.of(book));
        when(lendingHistoryRepository.findByBookId(42)).thenReturn(Collections.emptyList());

        // Act
        DtoBookInfo result = tBookService.processBookSearchRequest(42, "999");

        // Assert: qrData ではなく bookIdParam の 42 が使われる
        assertThat(result).isNotNull();
        assertThat(result.getBookId()).isEqualTo(42);
    }

    @Test
    @DisplayName("processBookSearchRequest_正常_qrDataを数値変換して書籍を取得する")
    void processBookSearchRequest_usesQrDataWhenBookIdNull() {
        // Arrange: bookIdParam=null、qrData="55" のケース
        TBookEntity book = buildBook(55, "Tech", null);
        book.setTitle("From QR");
        when(tBookRepository.findById(55)).thenReturn(Optional.of(book));
        when(lendingHistoryRepository.findByBookId(55)).thenReturn(Collections.emptyList());

        // Act
        DtoBookInfo result = tBookService.processBookSearchRequest(null, "55");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBookId()).isEqualTo(55);
    }

    @Test
    @DisplayName("processBookSearchRequest_境界_bookIdParamもqrDataもnullならnullを返す")
    void processBookSearchRequest_bothNull_returnsNull() {
        // Arrange: 何もスタブしない（リポジトリにアクセスする前に null 判定で抜けるため）

        // Act
        DtoBookInfo result = tBookService.processBookSearchRequest(null, null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("processBookSearchRequest_異常_qrDataが非数値ならnullを返す")
    void processBookSearchRequest_invalidQrData_returnsNull() {
        // Arrange: 非数値の QR データ（内部で IllegalArgumentException が投げられ、catch されて null になる）

        // Act
        DtoBookInfo result = tBookService.processBookSearchRequest(null, "not-a-number");

        // Assert
        assertThat(result).isNull();
    }

    // ─── tbookconfirm ────────────────────────────────────────────────────────

    @Test
    @DisplayName("tbookconfirm_正常_画像ファイル指定時はsavetbookを呼びURLをセット")
    void tbookconfirm_savesImageWhenFileProvided() throws IOException {
        // Arrange
        MultipartFile imageFile = new MockMultipartFile(
            "file", "book.png", "image/png", "img".getBytes());
        DtoBookInfo bookDto = new DtoBookInfo();
        bookDto.setImageFile(imageFile);

        when(imageStorageService.savetbook(imageFile)).thenReturn("tmp_xxx.png");

        // Act
        tBookService.tbookconfirm(bookDto);

        // Assert: ストレージに保存され、戻り値の URL が DTO にセットされている
        verify(imageStorageService).savetbook(imageFile);
        assertThat(bookDto.getImageUrl()).isEqualTo("tmp_xxx.png");
    }

    @Test
    @DisplayName("tbookconfirm_境界_画像ファイルnullなら何もしない")
    void tbookconfirm_doesNothingWhenFileIsNull() throws IOException {
        // Arrange
        DtoBookInfo bookDto = new DtoBookInfo();
        bookDto.setImageFile(null);

        // Act
        tBookService.tbookconfirm(bookDto);

        // Assert: ストレージサービスは一度も呼ばれない
        verify(imageStorageService, never()).savetbook(any());
        assertThat(bookDto.getImageUrl()).isNull();
    }

    @Test
    @DisplayName("tbookconfirm_境界_画像ファイル空なら何もしない")
    void tbookconfirm_doesNothingWhenFileIsEmpty() throws IOException {
        // Arrange: サイズ0の MultipartFile
        MultipartFile emptyFile = new MockMultipartFile(
            "file", "empty.png", "image/png", new byte[0]);
        DtoBookInfo bookDto = new DtoBookInfo();
        bookDto.setImageFile(emptyFile);

        // Act
        tBookService.tbookconfirm(bookDto);

        // Assert
        verify(imageStorageService, never()).savetbook(any());
        assertThat(bookDto.getImageUrl()).isNull();
    }

    // ─── bookEditing ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("bookEditing_正常_既存カテゴリヒット時はmaxId+1で採番")
    void bookEditing_existingCategory_assignsNextId() {
        // Arrange: 既存カテゴリ "Tech" の最大ID は 10001 → 次は 10002 になる想定
        DtoBookInfo inputDto = buildBookInfo("Tech", "7", "良い本");
        inputDto.setTitle("New Tech Book");
        inputDto.setImageUrl(null);
        TUserEntity provider = new TUserEntity();
        provider.setUserId(7);
        provider.setName("AliceProvider");
        provider.setDeleteFlg(Const.DELETE_FLAG_OFF);

        when(tUserRepository.findById(7)).thenReturn(Optional.of(provider));
        when(tBookRepository.findMaxIdByName("Tech")).thenReturn(10001);
        when(tBookRepository.save(any(TBookEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<TBookEntity> captor = ArgumentCaptor.forClass(TBookEntity.class);

        // Act
        DtoBookInfo result = tBookService.bookEditing(inputDto);

        // Assert: 既存カテゴリの末尾 +1 = 10002 で採番されること
        verify(tBookRepository).save(captor.capture());
        assertThat(captor.getValue().getBookId()).isEqualTo(10002);
        assertThat(result.getBookId()).isEqualTo(10002);
    }

    @Test
    @DisplayName("bookEditing_正常_新規カテゴリでmaxBookIdありなら全体max+10001で採番")
    void bookEditing_newCategoryWithExistingBooks_assignsNextNewId() {
        // Arrange: カテゴリ "AI" は新規（findMaxIdByName=null）、全体最大IDは 20001
        DtoBookInfo inputDto = buildBookInfo("AI", "7", "コメント");
        inputDto.setTitle("New AI Book");
        inputDto.setImageUrl(null);
        TUserEntity provider = new TUserEntity();
        provider.setUserId(7);
        provider.setName("AliceProvider");
        provider.setDeleteFlg(Const.DELETE_FLAG_OFF);

        when(tUserRepository.findById(7)).thenReturn(Optional.of(provider));
        when(tBookRepository.findMaxIdByName("AI")).thenReturn(null);
        when(tBookRepository.findMaxBookId()).thenReturn(20001);
        when(tBookRepository.save(any(TBookEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<TBookEntity> captor = ArgumentCaptor.forClass(TBookEntity.class);

        // Act
        tBookService.bookEditing(inputDto);

        // Assert: 20001 + PLUS_NEWBOOKID(10001) = 30002
        verify(tBookRepository).save(captor.capture());
        assertThat(captor.getValue().getBookId()).isEqualTo(30002);
    }

    @Test
    @DisplayName("bookEditing_境界_書籍0件かつ新規カテゴリでも10001で採番")
    void bookEditing_emptyDb_assignsBaseNewId() {
        // Arrange: DB が空（findMaxBookId=null）かつ新規カテゴリ
        DtoBookInfo inputDto = buildBookInfo("Initial", "7", "コメント");
        inputDto.setTitle("First Book Ever");
        inputDto.setImageUrl(null);
        TUserEntity provider = new TUserEntity();
        provider.setUserId(7);
        provider.setName("AliceProvider");
        provider.setDeleteFlg(Const.DELETE_FLAG_OFF);

        when(tUserRepository.findById(7)).thenReturn(Optional.of(provider));
        when(tBookRepository.findMaxIdByName("Initial")).thenReturn(null);
        when(tBookRepository.findMaxBookId()).thenReturn(null);
        when(tBookRepository.save(any(TBookEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<TBookEntity> captor = ArgumentCaptor.forClass(TBookEntity.class);

        // Act
        tBookService.bookEditing(inputDto);

        // Assert: 0 + PLUS_NEWBOOKID(10001) = 10001
        verify(tBookRepository).save(captor.capture());
        assertThat(captor.getValue().getBookId()).isEqualTo(10001);
    }

    @Test
    @DisplayName("bookEditing_異常_提供者ユーザーIDが存在しない場合はRuntimeExceptionをスロー")
    void bookEditing_providerNotFound_throwsRuntimeException() {
        // Arrange: ユーザーIDがユーザーマスタにない
        DtoBookInfo inputDto = buildBookInfo("Tech", "999", "コメント");
        when(tUserRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> tBookService.bookEditing(inputDto))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("ユーザーが見つかりません");

        // 補足検証: 提供者解決の時点で弾かれるため save は呼ばれない
        verify(tBookRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookEditing_異常_論理削除済みユーザーを選んだ場合はIllegalArgumentException")
    void bookEditing_deletedProvider_throwsIllegalArgumentException() {
        DtoBookInfo inputDto = buildBookInfo("Tech", "7", "コメント");
        TUserEntity provider = new TUserEntity();
        provider.setUserId(7);
        provider.setName("DeletedUser");
        provider.setDeleteFlg(Const.DELETE_FLAG_ON);
        when(tUserRepository.findById(7)).thenReturn(Optional.of(provider));

        assertThatThrownBy(() -> tBookService.bookEditing(inputDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("選択されたユーザーは利用できません");

        verify(tBookRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookEditing_異常_提供者IDが数値でない場合はIllegalArgumentException")
    void bookEditing_invalidProviderId_throwsIllegalArgumentException() {
        DtoBookInfo inputDto = buildBookInfo("Tech", "not-an-id", "コメント");

        assertThatThrownBy(() -> tBookService.bookEditing(inputDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("書籍提供者の指定が不正です");

        verify(tUserRepository, never()).findById(anyInt());
        verify(tBookRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookEditing_正常_画像リネーム失敗してもDB登録は完了する")
    void bookEditing_imageRenameFails_butSaveSucceeds() throws Exception {
        // Arrange: 画像ファイル名は指定されているが、リネーム時に例外が発生する
        DtoBookInfo inputDto = buildBookInfo("Tech", "7", "コメント");
        inputDto.setTitle("Resilient Save");
        inputDto.setImageUrl("tmp_xxx.png");
        TUserEntity provider = new TUserEntity();
        provider.setUserId(7);
        provider.setName("AliceProvider");
        provider.setDeleteFlg(Const.DELETE_FLAG_OFF);

        when(tUserRepository.findById(7)).thenReturn(Optional.of(provider));
        when(tBookRepository.findMaxIdByName("Tech")).thenReturn(10001);
        when(tBookRepository.save(any(TBookEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        // リネームで例外（画像処理は失敗するが DB 登録は継続される仕様）
        doThrow(new IOException("rename failed"))
            .when(imageStorageService).renameToBookId("tmp_xxx.png", 10002);

        // Act
        DtoBookInfo result = tBookService.bookEditing(inputDto);

        // Assert: 例外は伝播せず、DTOが返ってくる
        assertThat(result).isNotNull();
        assertThat(result.getBookId()).isEqualTo(10002);
        verify(tBookRepository).save(any(TBookEntity.class));
    }

    @Test
    @DisplayName("bookEditing_異常_DataIntegrityViolationExceptionはIllegalStateExceptionへラップ")
    void bookEditing_dataIntegrityViolation_wrappedAsIllegalState() {
        // Arrange: save 時に DB の一意制約違反が発生
        DtoBookInfo inputDto = buildBookInfo("Tech", "7", "コメント");
        inputDto.setTitle("Conflict");
        TUserEntity provider = new TUserEntity();
        provider.setUserId(7);
        provider.setName("AliceProvider");
        provider.setDeleteFlg(Const.DELETE_FLAG_OFF);

        when(tUserRepository.findById(7)).thenReturn(Optional.of(provider));
        when(tBookRepository.findMaxIdByName("Tech")).thenReturn(10001);
        when(tBookRepository.save(any(TBookEntity.class)))
            .thenThrow(new DataIntegrityViolationException("duplicate"));

        // Act & Assert: ユーザー向けには IllegalStateException("登録に失敗しました") に変換される
        assertThatThrownBy(() -> tBookService.bookEditing(inputDto))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("登録に失敗しました");
    }

    // ─── deleteBookById ───────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteBookById_正常_書籍と履歴と画像を削除してtrueを返す")
    void deleteBookById_deletesAndReturnsTrue() throws Exception {
        // Arrange: 貸出されていない書籍
        final Integer targetBookId = 1;
        TBookEntity book = buildBook(targetBookId, "Tech", null);
        when(tBookRepository.findById(targetBookId)).thenReturn(Optional.of(book));

        // Act
        boolean result = tBookService.deleteBookById(targetBookId);

        // Assert: 履歴・本体・画像すべてが削除される
        assertThat(result).isTrue();
        verify(lendingHistoryRepository).deleteByBookId(targetBookId);
        verify(tBookRepository).deleteById(targetBookId);
        verify(imageStorageService).deleteImage(targetBookId);
    }

    @Test
    @DisplayName("deleteBookById_異常_貸出中の書籍は削除できずIllegalStateException")
    void deleteBookById_throwsWhenBorrowed() {
        // Arrange: 借りられている書籍（borrowerId が設定されている）
        TBookEntity borrowedBook = buildBook(1, "Tech", 5);
        when(tBookRepository.findById(1)).thenReturn(Optional.of(borrowedBook));

        // Act & Assert
        assertThatThrownBy(() -> tBookService.deleteBookById(1))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("貸出中");

        // 補足検証: DB 削除は実行されない
        verify(tBookRepository, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("deleteBookById_異常_存在しないIDはfalseを返す")
    void deleteBookById_notFound_returnsFalse() {
        // Arrange
        final Integer missingBookId = 999;
        when(tBookRepository.findById(missingBookId)).thenReturn(Optional.empty());

        // Act
        boolean result = tBookService.deleteBookById(missingBookId);

        // Assert: 例外ではなく false が返り、削除処理も走らない
        assertThat(result).isFalse();
        verify(tBookRepository, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("deleteBookById_正常_画像削除が例外でもDB削除は継続される")
    void deleteBookById_imageDeleteFails_continuesDbDelete() throws Exception {
        // Arrange: 画像削除が例外を投げる状況
        final Integer targetBookId = 1;
        TBookEntity book = buildBook(targetBookId, "Tech", null);
        when(tBookRepository.findById(targetBookId)).thenReturn(Optional.of(book));
        doThrow(new RuntimeException("image io error"))
            .when(imageStorageService).deleteImage(targetBookId);

        // Act
        boolean result = tBookService.deleteBookById(targetBookId);

        // Assert: 画像削除エラーは握りつぶされ、本体と履歴の削除は継続される
        assertThat(result).isTrue();
        verify(lendingHistoryRepository).deleteByBookId(targetBookId);
        verify(tBookRepository).deleteById(targetBookId);
    }

    // ─── getCategoriesdetail ──────────────────────────────────────────────────

    @Test
    @DisplayName("getCategoriesdetail_正常_カテゴリに含まれる書籍IDのリストを返す")
    void getCategoriesdetail_returnsMatchingBookIds() {
        // Arrange: 3冊のうち "Java" を含む本は 1冊目と 3冊目
        TBookEntity book1 = buildBook(1, "Tech,Java", null);
        TBookEntity book2 = buildBook(2, "Design", null);
        TBookEntity book3 = buildBook(3, "Java,Spring", null);
        when(tBookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));

        // Act
        List<Integer> result = tBookService.getCategoriesdetail("Java");

        // Assert
        assertThat(result).containsExactlyInAnyOrder(1, 3);
    }

    // ─── getPagedBooksByCategory / getBooksByCategoryTotalPages ──────────────

    @Test
    @DisplayName("getPagedBooksByCategory_境界_範囲外ページで空リストを返す")
    void getPagedBooksByCategory_outOfRangePage_returnsEmptyList() {
        // Arrange: 該当カテゴリの本は1冊だけ
        TBookEntity book = buildBook(1, "Tech", null);
        book.setTitle("Only Book");
        when(tBookRepository.findAll()).thenReturn(Collections.singletonList(book));
        when(lendingHistoryRepository.findByBookId(1)).thenReturn(Collections.emptyList());

        // Act: 10ページ目を 6件ずつ → 範囲外
        List<DtoBookInfo> result = tBookService.getPagedBooksByCategory("Tech", 10, 6);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getBooksByCategoryTotalPages_境界_カテゴリ該当0件で総ページ数0")
    void getBooksByCategoryTotalPages_noMatch_returnsZero() {
        // Arrange: そもそもこのカテゴリに本が無い
        when(tBookRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        int result = tBookService.getBooksByCategoryTotalPages("Java", 6);

        // Assert
        assertThat(result).isZero();
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    /** テスト用: 最小フィールドだけ設定した TBookEntity を作る */
    private TBookEntity buildBook(Integer id, String categories, Integer borrowerId) {
        TBookEntity b = new TBookEntity();
        b.setBookId(id);
        b.setCategories(categories);
        b.setBorrowerId(borrowerId);
        return b;
    }

    /** テスト用: 書籍登録 DTO を作る（providerId はユーザーIDの文字列） */
    private DtoBookInfo buildBookInfo(String category, String providerUserId, String comment) {
        DtoBookInfo dto = new DtoBookInfo();
        dto.setCategory(category);
        dto.setProviderId(providerUserId);
        dto.setProviderComment(comment);
        return dto;
    }
}
