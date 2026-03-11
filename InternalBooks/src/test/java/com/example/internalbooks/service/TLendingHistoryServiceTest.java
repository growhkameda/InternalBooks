package com.example.internalbooks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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

import com.example.internalbooks.dto.DtoBookHistory;
import com.example.internalbooks.dto.DtoBookHistoryRegistration;
import com.example.internalbooks.entity.TLendingHistoryEntity;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.repository.TBookRepository;
import com.example.internalbooks.repository.TLendingHistoryRepository;
import com.example.internalbooks.repository.TUserRepository;

@ExtendWith(MockitoExtension.class)
class TLendingHistoryServiceTest {

    @Mock
    private TLendingHistoryRepository lendingHistoryRepository;

    @Mock
    private TUserRepository userRepository;

    @Mock
    private TBookRepository bookRepository;

    @InjectMocks
    private TLendingHistoryService tLendingHistoryService;

    // ─── rentalCompleted ──────────────────────────────────────────────────────

    @Test
    @DisplayName("rentalCompleted_正常_貸出日と返却予定日を設定して保存")
    void rentalCompleted_setsLendingAndReturnDates() {
        DtoBookHistoryRegistration dto = buildDto(10, 1);
        when(lendingHistoryRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        TLendingHistoryEntity result = tLendingHistoryService.rentalCompleted(dto);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertThat(result.getLendingDate()).isBetween(before, after);
        assertThat(result.getScheduledReturnDate()).isBetween(before.plusDays(7), after.plusDays(7));
        assertThat(result.getReturnDate()).isNull();
    }

    @Test
    @DisplayName("rentalCompleted_正常_本のborrowerId更新")
    void rentalCompleted_updatesBorrowerId() {
        DtoBookHistoryRegistration dto = buildDto(10, 1);
        when(lendingHistoryRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        tLendingHistoryService.rentalCompleted(dto);

        verify(bookRepository).updateBorrowerByBookId(10, 1);
    }

    // ─── returnCompleted ─────────────────────────────────────────────────────

    @Test
    @DisplayName("returnCompleted_正常_返却日を設定して保存")
    void returnCompleted_setsReturnDate() {
        DtoBookHistoryRegistration dto = buildDto(10, 1);
        TLendingHistoryEntity existing = new TLendingHistoryEntity();
        existing.setBookId(10);
        existing.setUserId(1);
        when(lendingHistoryRepository.findActiveLendingHistory(10, 1)).thenReturn(Optional.of(existing));
        when(lendingHistoryRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        TLendingHistoryEntity result = tLendingHistoryService.returnCompleted(dto);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertThat(result.getReturnDate()).isBetween(before, after);
    }

    @Test
    @DisplayName("returnCompleted_正常_borrowerIdをクリア")
    void returnCompleted_clearsBorrowerId() {
        DtoBookHistoryRegistration dto = buildDto(10, 1);
        TLendingHistoryEntity existing = new TLendingHistoryEntity();
        existing.setBookId(10);
        existing.setUserId(1);
        when(lendingHistoryRepository.findActiveLendingHistory(10, 1)).thenReturn(Optional.of(existing));
        when(lendingHistoryRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        tLendingHistoryService.returnCompleted(dto);

        verify(bookRepository).clearBorrowerByBookId(10);
    }

    // ─── getHistoryByBookId ───────────────────────────────────────────────────

    @Test
    @DisplayName("getHistoryByBookId_正常_日本語フォーマットで日付を返す")
    void getHistoryByBookId_formatsDateInJapanese() {
        TLendingHistoryEntity entity = new TLendingHistoryEntity();
        entity.setBookId(10);
        entity.setUserId(1);
        entity.setLendingDate(LocalDateTime.of(2024, 1, 15, 0, 0));
        entity.setScheduledReturnDate(LocalDateTime.of(2024, 1, 22, 0, 0));
        entity.setReturnDate(LocalDateTime.of(2024, 1, 20, 0, 0));

        TUserEntity user = new TUserEntity();
        user.setUserId(1);
        user.setName("田中太郎");

        when(lendingHistoryRepository.findByBookId(10)).thenReturn(Collections.singletonList(entity));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        List<DtoBookHistory> result = tLendingHistoryService.getHistoryByBookId(10);

        assertThat(result).hasSize(1);
        // 日本語フォーマット "yyyy年MM月dd日(E)" で整形されていることを確認
        assertThat(result.get(0).getLendingDate()).contains("2024年01月15日");
        assertThat(result.get(0).getScheduledReturnDate()).contains("2024年01月22日");
        assertThat(result.get(0).getReturnDate()).contains("2024年01月20日");
        assertThat(result.get(0).getUserName()).isEqualTo("田中太郎");
    }

    // ─── getScheduledReturnDatesByBookIds ─────────────────────────────────────

    @Test
    @DisplayName("getScheduledReturnDatesByBookIds_正常_複数書籍の返却予定日を返す")
    void getScheduledReturnDatesByBookIds_returnsCorrectDates() {
        TLendingHistoryEntity h1 = new TLendingHistoryEntity();
        h1.setScheduledReturnDate(LocalDateTime.of(2024, 3, 1, 0, 0));

        TLendingHistoryEntity h2 = new TLendingHistoryEntity();
        h2.setScheduledReturnDate(LocalDateTime.of(2024, 3, 10, 0, 0));

        when(lendingHistoryRepository.findByBookId(1)).thenReturn(Collections.singletonList(h1));
        when(lendingHistoryRepository.findByBookId(2)).thenReturn(Collections.singletonList(h2));
        when(lendingHistoryRepository.findByBookId(3)).thenReturn(Collections.emptyList());

        List<DtoBookHistory> result = tLendingHistoryService.getScheduledReturnDatesByBookIds(Arrays.asList(1, 2, 3));

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getScheduledReturnDate()).contains("2024年03月01日");
        assertThat(result.get(1).getScheduledReturnDate()).contains("2024年03月10日");
        assertThat(result.get(2).getScheduledReturnDate()).isEqualTo("-");
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private DtoBookHistoryRegistration buildDto(Integer bookId, Integer userId) {
        DtoBookHistoryRegistration dto = new DtoBookHistoryRegistration();
        dto.setBookId(bookId);
        dto.setUserId(userId);
        return dto;
    }
}
