package com.example.internalbooks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.internalbooks.dto.DtoUserEdit;
import com.example.internalbooks.dto.DtoUserRegistration;
import com.example.internalbooks.entity.MDepartmentEntity;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.repository.MDepartmentRepository;
import com.example.internalbooks.repository.TUserRepository;

/**
 * TUserService の単体テスト。
 *
 * テストの方針（初学者向けメモ）:
 * - 各テストは Arrange（準備） → Act（実行） → Assert（検証）の3段で記述する。
 * - 「正常系」「異常系」「境界値」のいずれを検証しているかを DisplayName に明記する。
 * - モック（@Mock）は外部依存（DB やパスワードエンコーダ）の代わりに使う。
 *   実物の DB を使わずに、Service クラスのロジックだけを検証することが目的。
 */
@ExtendWith(MockitoExtension.class)
class TUserServiceTest {

    @Mock
    private TUserRepository tUserRepository;

    @Mock
    private MDepartmentRepository mDepartmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TUserService tUserService;

    // ─── loadUserByUsername ───────────────────────────────────────────────────

    @Test
    @DisplayName("loadUserByUsername_正常_メールアドレスでユーザー情報が取得できる")
    void loadUserByUsername_success() {
        // Arrange: メールアドレスでユーザーが見つかる状態をモックで再現する
        final String existingMail = "test@example.com";
        TUserEntity registeredUser = buildUser(1, existingMail, 0);
        when(tUserRepository.findByMailAddress(existingMail)).thenReturn(Optional.of(registeredUser));

        // Act: テスト対象メソッドを呼び出す
        TUserEntity result = tUserService.loadUserByUsername(existingMail);

        // Assert: リポジトリから取得したユーザーがそのまま返ってくる
        assertThat(result).isEqualTo(registeredUser);
    }

    @Test
    @DisplayName("loadUserByUsername_異常_ユーザー未存在でNoSuchElementExceptionをスロー")
    void loadUserByUsername_notFound_throwsNoSuchElementException() {
        // Arrange: ユーザーが見つからない状態をモックで再現する
        // 実装が Optional.get() を呼ぶため、空 Optional だと NoSuchElementException になる
        final String missingMail = "missing@example.com";
        when(tUserRepository.findByMailAddress(missingMail)).thenReturn(Optional.empty());

        // Act & Assert: Optional.get() による NoSuchElementException が発生する
        assertThatThrownBy(() -> tUserService.loadUserByUsername(missingMail))
            .isInstanceOf(NoSuchElementException.class);
    }

    // ─── getUserById ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("getUserById_正常_IDで取得できる")
    void getUserById_success() {
        // Arrange: 指定ID でユーザーが見つかる状態をモックで再現する
        final Integer existingUserId = 1;
        TUserEntity registeredUser = buildUser(existingUserId, "test@example.com", 0);
        when(tUserRepository.findById(existingUserId)).thenReturn(Optional.of(registeredUser));

        // Act: テスト対象メソッドを呼び出す
        TUserEntity result = tUserService.getUserById(existingUserId);

        // Assert: リポジトリから取得したユーザーがそのまま返ってくる
        assertThat(result).isEqualTo(registeredUser);
    }

    @Test
    @DisplayName("getUserById_異常_未存在でnullを返す")
    void getUserById_notFound_returnsNull() {
        // Arrange: 指定ID のユーザーが存在しない状態をモックで再現する
        final Integer missingUserId = 999;
        when(tUserRepository.findById(missingUserId)).thenReturn(Optional.empty());

        // Act
        TUserEntity result = tUserService.getUserById(missingUserId);

        // Assert: 例外ではなく null が返る仕様
        assertThat(result).isNull();
    }

    // ─── getAllUsers ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllUsers_正常_全ユーザーをリポジトリから取得して返す")
    void getAllUsers_returnsAllUsersFromRepository() {
        // Arrange: 削除済みも含む全ユーザーをモックで返す
        TUserEntity activeUser = buildUser(1, "a@example.com", 0);
        TUserEntity deletedUser = buildUser(2, "b@example.com", 1);
        when(tUserRepository.findAll()).thenReturn(Arrays.asList(activeUser, deletedUser));

        // Act
        List<TUserEntity> result = tUserService.getAllUsers();

        // Assert: getAllUsers は削除済みでフィルタしない（findAll そのまま）
        assertThat(result).containsExactly(activeUser, deletedUser);
    }

    // ─── getActiveUsers ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getActiveUsers_正常_削除済みを除外して返す")
    void getActiveUsers_returnsOnlyActiveUsers() {
        // Arrange: アクティブ（deleteFlg=0）のユーザーのみ返す状態を作る
        TUserEntity active1 = buildUser(1, "a@example.com", 0);
        TUserEntity active2 = buildUser(2, "b@example.com", 0);
        when(tUserRepository.findByDeleteFlg(0)).thenReturn(Arrays.asList(active1, active2));

        // Act
        List<TUserEntity> result = tUserService.getActiveUsers();

        // Assert: deleteFlg=0 のユーザーがそのまま返る
        assertThat(result).hasSize(2).containsExactlyInAnyOrder(active1, active2);
    }

    @Test
    @DisplayName("getActiveUsersSortedByName_正常_削除フラグ0かつ名前昇順で返す")
    void getActiveUsersSortedByName_returnsSortedActiveUsers() {
        TUserEntity bob = buildUser(2, "b@example.com", 0);
        bob.setName("Bob");
        TUserEntity ann = buildUser(1, "a@example.com", 0);
        ann.setName("Ann");
        when(tUserRepository.findByDeleteFlgOrderByNameAsc(0)).thenReturn(Arrays.asList(ann, bob));

        List<TUserEntity> result = tUserService.getActiveUsersSortedByName();

        assertThat(result).containsExactly(ann, bob);
        verify(tUserRepository).findByDeleteFlgOrderByNameAsc(0);
    }

    // ─── getUsersExceptCurrent ────────────────────────────────────────────────

    @Test
    @DisplayName("getUsersExceptCurrent_正常_自分を除外したリストを返す")
    void getUsersExceptCurrent_excludesCurrentUser() {
        // Arrange: ログインユーザーと別のユーザーが存在する状態を作る
        final Integer currentUserId = 1;
        TUserEntity me = buildUser(currentUserId, "me@example.com", 0);
        TUserEntity other = buildUser(2, "other@example.com", 0);
        when(tUserRepository.findByDeleteFlg(0)).thenReturn(Arrays.asList(me, other));

        // Act
        List<TUserEntity> result = tUserService.getUsersExceptCurrent(currentUserId);

        // Assert: ログインユーザー自身は除外され、他ユーザーのみ返る
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(2);
    }

    // ─── getDepartmentNameById ────────────────────────────────────────────────

    @Test
    @DisplayName("getDepartmentNameById_正常_部署名を返す")
    void getDepartmentNameById_returnsName() {
        // Arrange: 部署IDから部署名が引ける状態を作る
        final Integer existingDepartmentId = 10;
        when(mDepartmentRepository.findNameById(existingDepartmentId)).thenReturn(Optional.of("開発部"));

        // Act
        String result = tUserService.getDepartmentNameById(existingDepartmentId);

        // Assert
        assertThat(result).isEqualTo("開発部");
    }

    @Test
    @DisplayName("getDepartmentNameById_境界_nullで未設定を返す")
    void getDepartmentNameById_nullId_returnsUnset() {
        // Arrange: 引数 null のときはリポジトリにアクセスせず固定文言を返す仕様
        // （リポジトリの stub は不要 ＝ 不要な when() を書かない）

        // Act
        String result = tUserService.getDepartmentNameById(null);

        // Assert
        assertThat(result).isEqualTo("未設定");
    }

    @Test
    @DisplayName("getDepartmentNameById_異常_未存在で不明を返す")
    void getDepartmentNameById_notFound_returnsUnknown() {
        // Arrange: 部署IDが見つからない状態
        final Integer missingDepartmentId = 999;
        when(mDepartmentRepository.findNameById(missingDepartmentId)).thenReturn(Optional.empty());

        // Act
        String result = tUserService.getDepartmentNameById(missingDepartmentId);

        // Assert: 例外を投げず "不明" が返る
        assertThat(result).isEqualTo("不明");
    }

    // ─── getUserId(String) ───────────────────────────────────────────────────

    @Test
    @DisplayName("getUserId_正常_数値文字列をIntegerへ変換してOptionalで返す")
    void getUserId_numericString_returnsOptional() {
        // Arrange: 文字列ID "5" の Integer 変換結果でユーザーが見つかる状態
        final String userIdAsString = "5";
        final Integer userIdAsInteger = 5;
        TUserEntity registeredUser = buildUser(userIdAsInteger, "x@example.com", 0);
        when(tUserRepository.findById(userIdAsInteger)).thenReturn(Optional.of(registeredUser));

        // Act
        Optional<TUserEntity> result = tUserService.getUserId(userIdAsString);

        // Assert
        assertThat(result).isPresent().contains(registeredUser);
    }

    @Test
    @DisplayName("getUserId_異常_非数値文字列でNumberFormatExceptionをスロー")
    void getUserId_nonNumericString_throwsNumberFormatException() {
        // Arrange: Integer.valueOf がパースに失敗する文字列
        final String invalidId = "abc";

        // Act & Assert: 数値変換に失敗するため例外
        assertThatThrownBy(() -> tUserService.getUserId(invalidId))
            .isInstanceOf(NumberFormatException.class);
    }

    // ─── getUserDepartmentName ────────────────────────────────────────────────

    @Test
    @DisplayName("getUserDepartmentName_正常_各ユーザーに部署名を設定したリストを返す")
    void getUserDepartmentName_setsDepartmentName() {
        // Arrange: 自分以外の2ユーザー、それぞれの部署名解決結果を準備
        final Integer currentUserId = 1;
        TUserEntity me = buildUser(currentUserId, "me@example.com", 0);
        TUserEntity userInDev = buildUser(2, "dev@example.com", 0);
        userInDev.setDepartmentId(10);
        TUserEntity userInSales = buildUser(3, "sales@example.com", 0);
        userInSales.setDepartmentId(20);

        when(tUserRepository.findByDeleteFlg(0)).thenReturn(Arrays.asList(me, userInDev, userInSales));
        when(mDepartmentRepository.findNameById(10)).thenReturn(Optional.of("開発部"));
        when(mDepartmentRepository.findNameById(20)).thenReturn(Optional.of("営業部"));

        // Act
        List<TUserEntity> result = tUserService.getUserDepartmentName(currentUserId);

        // Assert: 自分は除外され、各ユーザーに部署名がセットされる
        assertThat(result).hasSize(2);
        assertThat(result).extracting(TUserEntity::getDepartmentName)
                          .containsExactly("開発部", "営業部");
    }

    @Test
    @DisplayName("getUserDepartmentName_境界_部署IDがnullのユーザーには未設定をセット")
    void getUserDepartmentName_nullDepartmentId_setsUnset() {
        // Arrange: 部署IDがnullのユーザー（部署未割り当て）が存在するケース
        final Integer currentUserId = 1;
        TUserEntity me = buildUser(currentUserId, "me@example.com", 0);
        TUserEntity userWithoutDept = buildUser(2, "noDept@example.com", 0);
        userWithoutDept.setDepartmentId(null);

        when(tUserRepository.findByDeleteFlg(0)).thenReturn(Arrays.asList(me, userWithoutDept));

        // Act
        List<TUserEntity> result = tUserService.getUserDepartmentName(currentUserId);

        // Assert: 部署IDが null のユーザーには「未設定」が入る（findNameById は呼ばれない）
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartmentName()).isEqualTo("未設定");
    }

    // ─── getUserWithDepartmentNameById ────────────────────────────────────────

    @Test
    @DisplayName("getUserWithDepartmentNameById_正常_ユーザー情報に部署名をセットして返す")
    void getUserWithDepartmentNameById_setsDepartmentName() {
        // Arrange
        final Integer existingUserId = 1;
        TUserEntity registeredUser = buildUser(existingUserId, "test@example.com", 0);
        registeredUser.setDepartmentId(10);
        when(tUserRepository.findById(existingUserId)).thenReturn(Optional.of(registeredUser));
        when(mDepartmentRepository.findNameById(10)).thenReturn(Optional.of("開発部"));

        // Act
        TUserEntity result = tUserService.getUserWithDepartmentNameById(existingUserId);

        // Assert: 部署名が反映されている
        assertThat(result.getDepartmentName()).isEqualTo("開発部");
    }

    @Test
    @DisplayName("getUserWithDepartmentNameById_異常_ユーザー未存在でnullを返す")
    void getUserWithDepartmentNameById_userNotFound_returnsNull() {
        // Arrange: 指定IDのユーザーが存在しない
        final Integer missingUserId = 999;
        when(tUserRepository.findById(missingUserId)).thenReturn(Optional.empty());

        // Act
        TUserEntity result = tUserService.getUserWithDepartmentNameById(missingUserId);

        // Assert: 部署名解決を試みず null を返す
        assertThat(result).isNull();
    }

    // ─── getUserEditDtoById ───────────────────────────────────────────────────

    @Test
    @DisplayName("getUserEditDtoById_正常_TUserEntityをDtoUserEditへ詰め替えて返す")
    void getUserEditDtoById_convertsEntityToDto() {
        // Arrange
        final Integer existingUserId = 1;
        TUserEntity registeredUser = buildUser(existingUserId, "test@example.com", 0);
        registeredUser.setName("田中太郎");
        registeredUser.setDepartmentId(10);
        when(tUserRepository.findById(existingUserId)).thenReturn(Optional.of(registeredUser));

        // Act
        DtoUserEdit result = tUserService.getUserEditDtoById(existingUserId);

        // Assert: Entity の各フィールドが DTO へ正しく詰め替えられている
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(existingUserId);
        assertThat(result.getName()).isEqualTo("田中太郎");
        assertThat(result.getMailAddress()).isEqualTo("test@example.com");
        assertThat(result.getDepartmentId()).isEqualTo("10");
    }

    @Test
    @DisplayName("getUserEditDtoById_異常_ユーザー未存在でnullを返す")
    void getUserEditDtoById_userNotFound_returnsNull() {
        // Arrange
        final Integer missingUserId = 999;
        when(tUserRepository.findById(missingUserId)).thenReturn(Optional.empty());

        // Act
        DtoUserEdit result = tUserService.getUserEditDtoById(missingUserId);

        // Assert: ユーザーがいないので DTO も返さない
        assertThat(result).isNull();
    }

    // ─── getAllDepartments ────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllDepartments_正常_リポジトリのfindAll結果をそのまま返す")
    void getAllDepartments_returnsAllDepartments() {
        // Arrange: 部署マスタのデータをモックで返す
        MDepartmentEntity dev = new MDepartmentEntity();
        dev.setId(10);
        MDepartmentEntity sales = new MDepartmentEntity();
        sales.setId(20);
        when(mDepartmentRepository.findAll()).thenReturn(Arrays.asList(dev, sales));

        // Act
        List<MDepartmentEntity> result = tUserService.getAllDepartments();

        // Assert
        assertThat(result).containsExactly(dev, sales);
    }

    // ─── userRegistration ─────────────────────────────────────────────────────

    @Test
    @DisplayName("userRegistration_正常_パスワードをエンコードして保存しDTOを返す")
    void userRegistration_encodesPasswordAndSaves() {
        // Arrange: 部署が見つかり、パスワード暗号化が機能する状態
        final String departmentName = "開発部";
        final String registeredMail = "alice@example.com";
        DtoUserRegistration inputDto = buildRegistrationDto("1", "Alice", registeredMail, departmentName);

        MDepartmentEntity dept = new MDepartmentEntity();
        dept.setId(1);
        when(mDepartmentRepository.findIdByName(departmentName)).thenReturn(Optional.of(dept));
        when(passwordEncoder.encode(registeredMail)).thenReturn("hashed");
        when(tUserRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        DtoUserRegistration result = tUserService.userRegistration(inputDto);

        // Assert: パスワードはメールアドレスでエンコード、Entity が保存され、結果DTOが返る
        verify(passwordEncoder).encode(registeredMail);
        verify(tUserRepository).save(any(TUserEntity.class));
        assertThat(result.getName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("userRegistration_正常_role/deleteFlgのデフォルト値が保存される")
    void userRegistration_defaultsRoleAndDeleteFlg() {
        // Arrange: DTO は role / deleteFlg のデフォルト値（=0）を持つ
        DtoUserRegistration inputDto = buildRegistrationDto("1", "Alice", "alice@example.com", "開発部");

        MDepartmentEntity dept = new MDepartmentEntity();
        dept.setId(1);
        when(mDepartmentRepository.findIdByName("開発部")).thenReturn(Optional.of(dept));
        when(passwordEncoder.encode("alice@example.com")).thenReturn("hashed");
        when(tUserRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // ArgumentCaptor で実際に save に渡された Entity の中身を検証する
        ArgumentCaptor<TUserEntity> captor = ArgumentCaptor.forClass(TUserEntity.class);

        // Act
        tUserService.userRegistration(inputDto);

        // Assert: role と deleteFlg がデフォルトの 0 で保存されている
        verify(tUserRepository).save(captor.capture());
        TUserEntity savedEntity = captor.getValue();
        assertThat(savedEntity.getRole()).isEqualTo(0);
        assertThat(savedEntity.getDeleteFlg()).isEqualTo(0);
        assertThat(savedEntity.getDepartmentId()).isEqualTo(1);
    }

    @Test
    @DisplayName("userRegistration_異常_部署が見つからない場合はIllegalArgumentExceptionをスロー")
    void userRegistration_departmentNotFound_throwsIllegalArgumentException() {
        // Arrange: 入力された部署名がマスタに存在しない状態
        final String missingDepartmentName = "存在しない部署";
        DtoUserRegistration inputDto = buildRegistrationDto("1", "Alice", "alice@example.com", missingDepartmentName);
        when(mDepartmentRepository.findIdByName(missingDepartmentName)).thenReturn(Optional.empty());

        // Act & Assert: 部署解決失敗で IllegalArgumentException("部署なし")
        assertThatThrownBy(() -> tUserService.userRegistration(inputDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("部署なし");

        // 補足検証: 部署が見つからない時点で save は呼ばれない（保存処理にフォールスルーしないこと）
        verify(tUserRepository, never()).save(any());
    }

    // ─── updateUser ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateUser_正常_ユーザー情報を上書きして保存する")
    void updateUser_savesUpdatedUser() {
        // Arrange: 既存ユーザーがいて、編集後のメールが他人と被らない状態
        final Integer existingUserId = 1;
        final String oldMail = "old@example.com";
        final String newMail = "new@example.com";
        TUserEntity existingUser = buildUser(existingUserId, oldMail, 0);

        when(tUserRepository.findById(existingUserId)).thenReturn(Optional.of(existingUser));
        when(tUserRepository.findByMailAddress(newMail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(newMail)).thenReturn("newhash");
        when(tUserRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DtoUserEdit editDto = buildEditDto(existingUserId, "Bob", newMail, "2");

        // Act
        tUserService.updateUser(editDto);

        // Assert: 既存ユーザーの値が上書きされ、save が呼ばれる
        verify(tUserRepository).save(any(TUserEntity.class));
        assertThat(existingUser.getMailAddress()).isEqualTo(newMail);
        assertThat(existingUser.getPassword()).isEqualTo("newhash");
    }

    @Test
    @DisplayName("updateUser_正常_自分自身が同じメールを保存しても重複扱いにならない")
    void updateUser_sameUserSameMail_doesNotThrow() {
        // Arrange: 既存ユーザーが自分自身と同じメールアドレスで保存しようとする状況
        final Integer existingUserId = 1;
        final String myMail = "me@example.com";
        TUserEntity existingUser = buildUser(existingUserId, myMail, 0);

        when(tUserRepository.findById(existingUserId)).thenReturn(Optional.of(existingUser));
        // findByMailAddress が「同じユーザー（自分）」を返すケース
        when(tUserRepository.findByMailAddress(myMail)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(myMail)).thenReturn("hash");
        when(tUserRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DtoUserEdit editDto = buildEditDto(existingUserId, "Me", myMail, "2");

        // Act
        tUserService.updateUser(editDto);

        // Assert: 重複エラーにならず保存が走る
        verify(tUserRepository).save(any(TUserEntity.class));
    }

    @Test
    @DisplayName("updateUser_異常_ユーザーが存在しない場合はRuntimeExceptionをスロー")
    void updateUser_userNotFound_throwsRuntimeException() {
        // Arrange: 編集対象ユーザーが見つからない状態
        final Integer missingUserId = 999;
        when(tUserRepository.findById(missingUserId)).thenReturn(Optional.empty());

        DtoUserEdit editDto = buildEditDto(missingUserId, "X", "x@example.com", "1");

        // Act & Assert
        assertThatThrownBy(() -> tUserService.updateUser(editDto))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("ユーザーが存在しません");

        // 補足検証: ユーザーがいない時点で save は呼ばれない
        verify(tUserRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateUser_異常_他人がメールを使用中の場合はIllegalArgumentExceptionをスロー")
    void updateUser_mailUsedByOtherUser_throwsIllegalArgumentException() {
        // Arrange: 編集後のメールアドレスが「自分以外」のユーザーで使われている状態
        final Integer myUserId = 1;
        final Integer otherUserId = 2;
        final String duplicatedMail = "shared@example.com";
        TUserEntity myUser = buildUser(myUserId, "me@example.com", 0);
        TUserEntity otherUser = buildUser(otherUserId, duplicatedMail, 0);

        when(tUserRepository.findById(myUserId)).thenReturn(Optional.of(myUser));
        when(tUserRepository.findByMailAddress(duplicatedMail)).thenReturn(Optional.of(otherUser));

        DtoUserEdit editDto = buildEditDto(myUserId, "Me", duplicatedMail, "1");

        // Act & Assert
        assertThatThrownBy(() -> tUserService.updateUser(editDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("既に他のユーザーに使用されています");

        // 補足検証: 重複エラーで弾かれて save は呼ばれない
        verify(tUserRepository, never()).save(any());
    }

    // ─── deleteUser ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteUser_正常_リポジトリのDeleteUserByIdに委譲する")
    void deleteUser_callsRepositoryDelete() {
        // Arrange: 削除対象ユーザーIDを用意
        final Integer targetUserId = 1;

        // Act
        tUserService.deleteUser(targetUserId);

        // Assert: リポジトリの論理削除メソッドが呼ばれる
        verify(tUserRepository).DeleteUserById(targetUserId);
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    /** テスト用: 最小フィールドだけ設定した TUserEntity を作る */
    private TUserEntity buildUser(Integer id, String mail, Integer deleteFlg) {
        TUserEntity u = new TUserEntity();
        u.setUserId(id);
        u.setMailAddress(mail);
        u.setDeleteFlg(deleteFlg);
        return u;
    }

    /** テスト用: ユーザー登録 DTO を作る */
    private DtoUserRegistration buildRegistrationDto(String id, String name, String mail, String dept) {
        DtoUserRegistration dto = new DtoUserRegistration();
        dto.setUserId(id);
        dto.setName(name);
        dto.setMailAddress(mail);
        dto.setDepartmentId(dept);
        return dto;
    }

    /** テスト用: ユーザー編集 DTO を作る */
    private DtoUserEdit buildEditDto(Integer id, String name, String mail, String deptId) {
        DtoUserEdit dto = new DtoUserEdit();
        dto.setUserId(id);
        dto.setName(name);
        dto.setMailAddress(mail);
        dto.setDepartmentId(deptId);
        return dto;
    }
}
