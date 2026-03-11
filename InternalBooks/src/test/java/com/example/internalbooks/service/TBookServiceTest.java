package com.example.internalbooks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.example.internalbooks.dto.DtoBookInfo;
import com.example.internalbooks.entity.TBookEntity;
import com.example.internalbooks.entity.TLendingHistoryEntity;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.repository.TBookRepository;
import com.example.internalbooks.repository.TLendingHistoryRepository;
import com.example.internalbooks.repository.TUserRepository;

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
        TBookEntity b1 = buildBook(1, "A,B", null);
        TBookEntity b2 = buildBook(2, "B,C", null);
        when(tBookRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(b1, b2));

        List<String> result = tBookService.getAllCategories();

        assertThat(result).containsExactly("A", "B", "C");
    }

    // ─── getPagedCategories ───────────────────────────────────────────────────

    @Test
    @DisplayName("getPagedCategories_正常_ページ内のカテゴリを返す")
    void getPagedCategories_returnsCorrectPage() {
        TBookEntity b = buildBook(1, "A,B,C,D,E", null);
        when(tBookRepository.findAll(any(Sort.class))).thenReturn(Collections.singletonList(b));

        List<String> result = tBookService.getPagedCategories(0, 2);

        assertThat(result).containsExactly("A", "B");
    }

    // ─── getCategoryTotalPages ────────────────────────────────────────────────

    @Test
    @DisplayName("getCategoryTotalPages_正常_総ページ数を計算する")
    void getCategoryTotalPages_calculatesCorrectly() {
        TBookEntity b = buildBook(1, "A,B,C,D,E", null);
        when(tBookRepository.findAll(any(Sort.class))).thenReturn(Collections.singletonList(b));

        int result = tBookService.getCategoryTotalPages(2);

        assertThat(result).isEqualTo(3); // ceil(5/2) = 3
    }

    // ─── getBookById ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("getBookById_正常_書籍情報をDtoに変換する")
    void getBookById_convertsEntityToDto() {
        TBookEntity book = buildBook(100, "Tech,Java", null);
        book.setTitle("Spring Book");
        when(tBookRepository.findById(100)).thenReturn(Optional.of(book));
        when(lendingHistoryRepository.findByBookId(100)).thenReturn(Collections.emptyList());

        DtoBookInfo result = tBookService.getBookById(100);

        assertThat(result).isNotNull();
        assertThat(result.getBookId()).isEqualTo(100);
        assertThat(result.getTitle()).isEqualTo("Spring Book");
        assertThat(result.getCategories()).containsExactly("Tech", "Java");
    }

    @Test
    @DisplayName("getBookById_貸出中ステータスを正しく判定する")
    void getBookById_lendingStatus_whenBorrowerIdExists() {
        TBookEntity book = buildBook(100, "Tech", 5); // borrowerId = 5
        book.setTitle("Borrowed Book");
        when(tBookRepository.findById(100)).thenReturn(Optional.of(book));

        DtoBookInfo result = tBookService.getBookById(100);

        assertThat(result.getStatus()).isEqualTo("貸出中");
    }

    @Test
    @DisplayName("getBookById_貸出可能ステータスを正しく判定する")
    void getBookById_lendingStatus_whenBorrowerIdIsNull() {
        TBookEntity book = buildBook(100, "Tech", null); // borrowerId = null
        book.setTitle("Available Book");
        when(tBookRepository.findById(100)).thenReturn(Optional.of(book));
        when(lendingHistoryRepository.findByBookId(100)).thenReturn(Collections.emptyList());

        DtoBookInfo result = tBookService.getBookById(100);

        assertThat(result.getStatus()).isEqualTo("貸出可能");
    }

    // ─── deleteBookById ───────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteBookById_正常_書籍と履歴を削除してtrueを返す")
    void deleteBookById_deletesAndReturnsTrue() throws Exception {
        TBookEntity book = buildBook(1, "Tech", null);
        when(tBookRepository.findById(1)).thenReturn(Optional.of(book));

        boolean result = tBookService.deleteBookById(1);

        assertThat(result).isTrue();
        verify(lendingHistoryRepository).deleteByBookId(1);
        verify(tBookRepository).deleteById(1);
        verify(imageStorageService).deleteImage(1);
    }

    @Test
    @DisplayName("deleteBookById_貸出中は削除できない")
    void deleteBookById_throwsWhenBorrowed() {
        TBookEntity book = buildBook(1, "Tech", 5); // borrowerId = 5 (貸出中)
        when(tBookRepository.findById(1)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> tBookService.deleteBookById(1))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("貸出中");

        verify(tBookRepository, never()).deleteById(anyInt());
    }

    // ─── processBookSearchRequest ─────────────────────────────────────────────

    @Test
    @DisplayName("processBookSearchRequest_bookIdParam優先で取得する")
    void processBookSearchRequest_prefersBookIdParam() {
        TBookEntity book = buildBook(42, "Tech", null);
        book.setTitle("Direct Lookup");
        when(tBookRepository.findById(42)).thenReturn(Optional.of(book));
        when(lendingHistoryRepository.findByBookId(42)).thenReturn(Collections.emptyList());

        DtoBookInfo result = tBookService.processBookSearchRequest(42, "999");

        assertThat(result).isNotNull();
        assertThat(result.getBookId()).isEqualTo(42);
    }

    // ─── getCategoriesdetail ──────────────────────────────────────────────────

    @Test
    @DisplayName("getCategoriesdetail_正常_カテゴリに含まれる書籍IDを返す")
    void getCategoriesdetail_returnsMatchingBookIds() {
        TBookEntity b1 = buildBook(1, "Tech,Java", null);
        TBookEntity b2 = buildBook(2, "Design", null);
        TBookEntity b3 = buildBook(3, "Java,Spring", null);
        when(tBookRepository.findAll()).thenReturn(Arrays.asList(b1, b2, b3));

        List<Integer> result = tBookService.getCategoriesdetail("Java");

        assertThat(result).containsExactlyInAnyOrder(1, 3);
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private TBookEntity buildBook(Integer id, String categories, Integer borrowerId) {
        TBookEntity b = new TBookEntity();
        b.setBookId(id);
        b.setCategories(categories);
        b.setBorrowerId(borrowerId);
        return b;
    }
}
