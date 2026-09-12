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
import org.mockito.ArgumentCaptor;
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

/**
 * TLendingHistoryService の単体テスト。
 *
 * 初学者向けメモ:
 * - 日時を扱うテストは「実行直前/直後の時刻範囲に入っているか」で検証する（厳密な値で比較しない）。
 * - 副作用（DB更新など）は verify() を使い、何が呼ばれたかを確認する。
 */
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
    @DisplayName("rentalCompleted_正常_貸出日と7日後の返却予定日を設定して保存")
    void rentalCompleted_setsLendingAndReturnDates() {
        // Arrange
        DtoBookHistoryRegistration inputDto = buildDto(10, 1);
        when(lendingHistoryRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act: 実行直前と直後の時刻を取得しておき、setされた日時がその範囲に入るか確認する
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        TLendingHistoryEntity result = tLendingHistoryService.rentalCompleted(inputDto);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        // Assert: 貸出日は今、返却予定日は7日後、返却日は未設定
        assertThat(result.getLendingDate()).isBetween(before, after);
        assertThat(result.getScheduledReturnDate()).isBetween(before.plusDays(7), after.plusDays(7));
        assertThat(result.getReturnDate()).isNull();
    }

    @Test
    @DisplayName("rentalCompleted_正常_書籍のborrowerIdを更新する")
    void rentalCompleted_updatesBorrowerId() {
        // Arrange
        final Integer targetBookId = 10;
        final Integer borrowerUserId = 1;
        DtoBookHistoryRegistration inputDto = buildDto(targetBookId, borrowerUserId);
        when(lendingHistoryRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        tLendingHistoryService.rentalCompleted(inputDto);

        // Assert: 書籍テーブル側の borrowerId 更新が呼び出される
        verify(bookRepository).updateBorrowerByBookId(targetBookId, borrowerUserId);
    }

    // ─── returnCompleted ─────────────────────────────────────────────────────

    @Test
    @DisplayName("returnCompleted_正常_返却日を設定して保存する")
    void returnCompleted_setsReturnDate() {
        // Arrange: 既に未返却の履歴が存在する
        DtoBookHistoryRegistration inputDto = buildDto(10, 1);
        TLendingHistoryEntity existingHistory = new TLendingHistoryEntity();
        existingHistory.setBookId(10);
        existingHistory.setUserId(1);
        when(lendingHistoryRepository.findActiveLendingHistory(10, 1))
            .thenReturn(Optional.of(existingHistory));
        when(lendingHistoryRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        TLendingHistoryEntity result = tLendingHistoryService.returnCompleted(inputDto);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        // Assert: 返却日が「現在時刻」付近にセットされる
        assertThat(result.getReturnDate()).isBetween(before, after);
    }

    @Test
    @DisplayName("returnCompleted_正常_書籍のborrowerIdをクリアする")
    void returnCompleted_clearsBorrowerId() {
        // Arrange
        final Integer targetBookId = 10;
        DtoBookHistoryRegistration inputDto = buildDto(targetBookId, 1);
        TLendingHistoryEntity existingHistory = new TLendingHistoryEntity();
        existingHistory.setBookId(targetBookId);
        existingHistory.setUserId(1);
        when(lendingHistoryRepository.findActiveLendingHistory(targetBookId, 1))
            .thenReturn(Optional.of(existingHistory));
        when(lendingHistoryRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        tLendingHistoryService.returnCompleted(inputDto);

        // Assert: 書籍側の borrowerId クリアが呼ばれる
        verify(bookRepository).clearBorrowerByBookId(targetBookId);
    }

    @Test
    @DisplayName("returnCompleted_境界_アクティブ履歴が無い場合は新規Entityで保存する")
    void returnCompleted_noActiveHistory_savesNewEntity() {
        // Arrange: findActiveLendingHistory が empty を返す（履歴が無い状態）
        DtoBookHistoryRegistration inputDto = buildDto(10, 1);
        inputDto.setReview("良い本でした");
        when(lendingHistoryRepository.findActiveLendingHistory(10, 1)).thenReturn(Optional.empty());
        when(lendingHistoryRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<TLendingHistoryEntity> captor =
            ArgumentCaptor.forClass(TLendingHistoryEntity.class);

        // Act
        tLendingHistoryService.returnCompleted(inputDto);

        // Assert: 新しく作られたEntityに DTO の値がマッピングされて保存される
        verify(lendingHistoryRepository).saveAndFlush(captor.capture());
        TLendingHistoryEntity saved = captor.getValue();
        assertThat(saved.getBookId()).isEqualTo(10);
        assertThat(saved.getUserId()).isEqualTo(1);
        assertThat(saved.getReturnDate()).isNotNull();
        assertThat(saved.getReview()).isEqualTo("良い本でした");
    }

    @Test
    @DisplayName("returnCompleted_境界_DTOのlendingDate/scheduledReturnDateがnullなら既存値を維持")
    void returnCompleted_keepsExistingDatesWhenDtoIsNull() {
        // Arrange: 既存履歴は元の貸出日・返却予定日を持つ。DTO 側はそれらが null
        DtoBookHistoryRegistration inputDto = buildDto(10, 1);
        inputDto.setLendingDate(null);
        inputDto.setScheduledReturnDate(null);

        TLendingHistoryEntity existingHistory = new TLendingHistoryEntity();
        existingHistory.setBookId(10);
        existingHistory.setUserId(1);
        existingHistory.setLendingDate(LocalDateTime.of(2026, 1, 10, 0, 0));
        existingHistory.setScheduledReturnDate(LocalDateTime.of(2026, 1, 17, 0, 0));

        when(lendingHistoryRepository.findActiveLendingHistory(10, 1))
            .thenReturn(Optional.of(existingHistory));
        when(lendingHistoryRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        TLendingHistoryEntity result = tLendingHistoryService.returnCompleted(inputDto);

        // Assert: 既存の lendingDate / scheduledReturnDate が上書きされず維持されている
        assertThat(result.getLendingDate()).isEqualTo(LocalDateTime.of(2026, 1, 10, 0, 0));
        assertThat(result.getScheduledReturnDate()).isEqualTo(LocalDateTime.of(2026, 1, 17, 0, 0));
    }

    @Test
    @DisplayName("returnCompleted_境界_review未入力なら既存reviewを上書きしない")
    void returnCompleted_reviewBlank_doesNotOverwriteExisting() {
        // Arrange: 既存履歴に既にレビューがある
        DtoBookHistoryRegistration inputDto = buildDto(10, 1);
        inputDto.setReview(""); // 空文字 = 未入力扱い

        TLendingHistoryEntity existingHistory = new TLendingHistoryEntity();
        existingHistory.setBookId(10);
        existingHistory.setUserId(1);
        existingHistory.setReview("以前のレビュー");

        when(lendingHistoryRepository.findActiveLendingHistory(10, 1))
            .thenReturn(Optional.of(existingHistory));
        when(lendingHistoryRepository.saveAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        TLendingHistoryEntity result = tLendingHistoryService.returnCompleted(inputDto);

        // Assert: 既存のレビューが残っている（空文字で上書きされない仕様）
        assertThat(result.getReview()).isEqualTo("以前のレビュー");
    }

    // ─── getHistoryByBookId ───────────────────────────────────────────────────

    @Test
    @DisplayName("getHistoryByBookId_正常_日本語フォーマットで日付を返す")
    void getHistoryByBookId_formatsDateInJapanese() {
        // Arrange: すべての日付・ユーザーが揃った1件の履歴
        TLendingHistoryEntity history = new TLendingHistoryEntity();
        history.setBookId(10);
        history.setUserId(1);
        history.setLendingDate(LocalDateTime.of(2024, 1, 15, 0, 0));
        history.setScheduledReturnDate(LocalDateTime.of(2024, 1, 22, 0, 0));
        history.setReturnDate(LocalDateTime.of(2024, 1, 20, 0, 0));

        TUserEntity user = new TUserEntity();
        user.setUserId(1);
        user.setName("田中太郎");

        when(lendingHistoryRepository.findByBookId(10)).thenReturn(Collections.singletonList(history));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Act
        List<DtoBookHistory> result = tLendingHistoryService.getHistoryByBookId(10);

        // Assert: 日付は "yyyy年MM月dd日(曜日)" 形式に整形され、ユーザー名も反映
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLendingDate()).contains("2024年01月15日");
        assertThat(result.get(0).getScheduledReturnDate()).contains("2024年01月22日");
        assertThat(result.get(0).getReturnDate()).contains("2024年01月20日");
        assertThat(result.get(0).getUserName()).isEqualTo("田中太郎");
    }

    @Test
    @DisplayName("getHistoryByBookId_境界_日付がnullなら-またはnullがセットされる")
    void getHistoryByBookId_nullDates_returnsPlaceholder() {
        // Arrange: すべての日付が null（履歴登録直後など）
        TLendingHistoryEntity history = new TLendingHistoryEntity();
        history.setBookId(10);
        history.setUserId(1);
        history.setLendingDate(null);
        history.setScheduledReturnDate(null);
        history.setReturnDate(null);

        TUserEntity user = new TUserEntity();
        user.setUserId(1);
        user.setName("山田花子");

        when(lendingHistoryRepository.findByBookId(10)).thenReturn(Collections.singletonList(history));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Act
        List<DtoBookHistory> result = tLendingHistoryService.getHistoryByBookId(10);

        // Assert: 貸出日・返却予定日は "-"、返却日は null（実装の仕様）
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLendingDate()).isEqualTo("-");
        assertThat(result.get(0).getScheduledReturnDate()).isEqualTo("-");
        assertThat(result.get(0).getReturnDate()).isNull();
    }

    @Test
    @DisplayName("getHistoryByBookId_異常_ユーザー未存在で不明ユーザーが入る")
    void getHistoryByBookId_userNotFound_setsUnknownUser() {
        // Arrange: ユーザーIDに該当するユーザーが見つからない（削除済みなど）
        TLendingHistoryEntity history = new TLendingHistoryEntity();
        history.setBookId(10);
        history.setUserId(999);
        history.setLendingDate(LocalDateTime.of(2024, 1, 15, 0, 0));

        when(lendingHistoryRepository.findByBookId(10)).thenReturn(Collections.singletonList(history));
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        List<DtoBookHistory> result = tLendingHistoryService.getHistoryByBookId(10);

        // Assert: ユーザー名のフォールバック文言が入る
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserName()).isEqualTo("不明ユーザー");
    }

    // ─── getScheduledReturnDatesByBookIds ─────────────────────────────────────

    @Test
    @DisplayName("getScheduledReturnDatesByBookIds_正常_書籍ごとの返却予定日を順に返す")
    void getScheduledReturnDatesByBookIds_returnsCorrectDates() {
        // Arrange: 3冊分の履歴を準備（3冊目だけ履歴なし）
        TLendingHistoryEntity history1 = new TLendingHistoryEntity();
        history1.setScheduledReturnDate(LocalDateTime.of(2024, 3, 1, 0, 0));

        TLendingHistoryEntity history2 = new TLendingHistoryEntity();
        history2.setScheduledReturnDate(LocalDateTime.of(2024, 3, 10, 0, 0));

        when(lendingHistoryRepository.findByBookId(1)).thenReturn(Collections.singletonList(history1));
        when(lendingHistoryRepository.findByBookId(2)).thenReturn(Collections.singletonList(history2));
        when(lendingHistoryRepository.findByBookId(3)).thenReturn(Collections.emptyList());

        // Act
        List<DtoBookHistory> result = tLendingHistoryService
            .getScheduledReturnDatesByBookIds(Arrays.asList(1, 2, 3));

        // Assert: 入力順に対応した結果が返り、履歴なしの3冊目は "-"
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getScheduledReturnDate()).contains("2024年03月01日");
        assertThat(result.get(1).getScheduledReturnDate()).contains("2024年03月10日");
        assertThat(result.get(2).getScheduledReturnDate()).isEqualTo("-");
    }

    @Test
    @DisplayName("getScheduledReturnDatesByBookIds_境界_履歴ありでも返却予定日nullなら-をセット")
    void getScheduledReturnDatesByBookIds_nullScheduledDate_returnsDash() {
        // Arrange: 履歴は存在するが scheduledReturnDate が null
        TLendingHistoryEntity history = new TLendingHistoryEntity();
        history.setScheduledReturnDate(null);
        when(lendingHistoryRepository.findByBookId(1)).thenReturn(Collections.singletonList(history));

        // Act
        List<DtoBookHistory> result = tLendingHistoryService
            .getScheduledReturnDatesByBookIds(Collections.singletonList(1));

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getScheduledReturnDate()).isEqualTo("-");
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    /** テスト用: 貸出/返却用の DTO を作る */
    private DtoBookHistoryRegistration buildDto(Integer bookId, Integer userId) {
        DtoBookHistoryRegistration dto = new DtoBookHistoryRegistration();
        dto.setBookId(bookId);
        dto.setUserId(userId);
        return dto;
    }
}
