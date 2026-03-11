package com.example.internalbooks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.internalbooks.dto.DtoUserEdit;
import com.example.internalbooks.dto.DtoUserRegistration;
import com.example.internalbooks.entity.MDepartmentEntity;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.repository.MDepartmentRepository;
import com.example.internalbooks.repository.TUserRepository;

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
    @DisplayName("loadUserByUsername_正常_メールで取得できる")
    void loadUserByUsername_success() {
        TUserEntity user = buildUser(1, "test@example.com", 0);
        when(tUserRepository.findByMailAddress("test@example.com")).thenReturn(Optional.of(user));

        TUserEntity result = tUserService.loadUserByUsername("test@example.com");

        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("loadUserByUsername_異常_ユーザー未存在でNoSuchElementExceptionをスロー")
    void loadUserByUsername_notFound_throwsException() {
        when(tUserRepository.findByMailAddress("missing@example.com")).thenReturn(Optional.empty());

        // Optional.get() on empty → NoSuchElementException
        assertThatThrownBy(() -> tUserService.loadUserByUsername("missing@example.com"))
            .isInstanceOf(Exception.class);
    }

    // ─── getUserById ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("getUserById_正常_IDで取得できる")
    void getUserById_success() {
        TUserEntity user = buildUser(1, "test@example.com", 0);
        when(tUserRepository.findById(1)).thenReturn(Optional.of(user));

        TUserEntity result = tUserService.getUserById(1);

        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("getUserById_異常_未存在でnullを返す")
    void getUserById_notFound_returnsNull() {
        when(tUserRepository.findById(999)).thenReturn(Optional.empty());

        TUserEntity result = tUserService.getUserById(999);

        assertThat(result).isNull();
    }

    // ─── getActiveUsers ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getActiveUsers_正常_削除済みを除外して返す")
    void getActiveUsers_returnsOnlyActiveUsers() {
        TUserEntity active1 = buildUser(1, "a@example.com", 0);
        TUserEntity active2 = buildUser(2, "b@example.com", 0);
        when(tUserRepository.findByDeleteFlg(0)).thenReturn(Arrays.asList(active1, active2));

        List<TUserEntity> result = tUserService.getActiveUsers();

        assertThat(result).hasSize(2).containsExactlyInAnyOrder(active1, active2);
    }

    // ─── getUsersExceptCurrent ────────────────────────────────────────────────

    @Test
    @DisplayName("getUsersExceptCurrent_正常_自分を除外したリストを返す")
    void getUsersExceptCurrent_excludesCurrentUser() {
        TUserEntity me = buildUser(1, "me@example.com", 0);
        TUserEntity other = buildUser(2, "other@example.com", 0);
        when(tUserRepository.findByDeleteFlg(0)).thenReturn(Arrays.asList(me, other));

        List<TUserEntity> result = tUserService.getUsersExceptCurrent(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(2);
    }

    // ─── getDepartmentNameById ────────────────────────────────────────────────

    @Test
    @DisplayName("getDepartmentNameById_正常_部署名を返す")
    void getDepartmentNameById_returnsName() {
        when(mDepartmentRepository.findNameById(10)).thenReturn(Optional.of("開発部"));

        String result = tUserService.getDepartmentNameById(10);

        assertThat(result).isEqualTo("開発部");
    }

    @Test
    @DisplayName("getDepartmentNameById_nullで未設定を返す")
    void getDepartmentNameById_nullId_returnsUnset() {
        String result = tUserService.getDepartmentNameById(null);

        assertThat(result).isEqualTo("未設定");
    }

    @Test
    @DisplayName("getDepartmentNameById_未存在で不明を返す")
    void getDepartmentNameById_notFound_returnsUnknown() {
        when(mDepartmentRepository.findNameById(999)).thenReturn(Optional.empty());

        String result = tUserService.getDepartmentNameById(999);

        assertThat(result).isEqualTo("不明");
    }

    // ─── userRegistration ─────────────────────────────────────────────────────

    @Test
    @DisplayName("userRegistration_正常_パスワードエンコードして保存")
    void userRegistration_encodesPasswordAndSaves() {
        DtoUserRegistration dto = buildRegistrationDto("1", "Alice", "alice@example.com", "開発部");
        MDepartmentEntity dept = new MDepartmentEntity();
        dept.setId(1);
        when(mDepartmentRepository.findIdByName("開発部")).thenReturn(Optional.of(dept));
        when(passwordEncoder.encode("alice@example.com")).thenReturn("hashed");
        when(tUserRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TUserEntity result = tUserService.userRegistration(dto);

        verify(passwordEncoder).encode("alice@example.com");
        verify(tUserRepository).save(any(TUserEntity.class));
        assertThat(result.getPassword()).isEqualTo("hashed");
    }

    // ─── updateUser ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateUser_正常_ユーザー情報を更新")
    void updateUser_savesUpdatedUser() {
        TUserEntity existing = buildUser(1, "old@example.com", 0);
        when(tUserRepository.findById(1)).thenReturn(Optional.of(existing));
        when(tUserRepository.findByMailAddress("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("new@example.com")).thenReturn("newhash");
        when(tUserRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DtoUserEdit dto = buildEditDto(1, "Bob", "new@example.com", "2");
        tUserService.updateUser(dto);

        verify(tUserRepository).save(any(TUserEntity.class));
        assertThat(existing.getMailAddress()).isEqualTo("new@example.com");
        assertThat(existing.getPassword()).isEqualTo("newhash");
    }

    // ─── deleteUser ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteUser_正常_DeleteUserByIdを呼び出す")
    void deleteUser_callsRepositoryDelete() {
        tUserService.deleteUser(1);

        verify(tUserRepository).DeleteUserById(1);
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private TUserEntity buildUser(Integer id, String mail, Integer deleteFlg) {
        TUserEntity u = new TUserEntity();
        u.setUserId(id);
        u.setMailAddress(mail);
        u.setDeleteFlg(deleteFlg);
        return u;
    }

    private DtoUserRegistration buildRegistrationDto(String id, String name, String mail, String dept) {
        DtoUserRegistration dto = new DtoUserRegistration();
        dto.setUserId(id);
        dto.setName(name);
        dto.setMailAddress(mail);
        dto.setDepartmentId(dept);
        return dto;
    }

    private DtoUserEdit buildEditDto(Integer id, String name, String mail, String deptId) {
        DtoUserEdit dto = new DtoUserEdit();
        dto.setUserId(id);
        dto.setName(name);
        dto.setMailAddress(mail);
        dto.setDepartmentId(deptId);
        return dto;
    }
}
