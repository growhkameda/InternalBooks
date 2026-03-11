package com.example.internalbooks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.internalbooks.dto.DtoAuthRequest;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.utils.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    // ─── login ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login_正常_JWTトークンを返す")
    void login_returnsToken() {
        DtoAuthRequest req = buildRequest("user@example.com", "pass");
        TUserEntity user = buildUser(1, "user@example.com", 0, 0);
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(user);
        when(jwtUtil.generateToken("user@example.com", 1, false)).thenReturn("jwt-token");

        String result = authService.login(req);

        assertThat(result).isEqualTo("jwt-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("login_正常_管理者フラグをJWTに含める")
    void login_adminUser_generatesTokenWithIsAdminTrue() {
        DtoAuthRequest req = buildRequest("admin@example.com", "pass");
        TUserEntity admin = buildUser(2, "admin@example.com", 1, 0); // role=1
        when(userDetailsService.loadUserByUsername("admin@example.com")).thenReturn(admin);
        when(jwtUtil.generateToken("admin@example.com", 2, true)).thenReturn("admin-token");

        String result = authService.login(req);

        assertThat(result).isEqualTo("admin-token");
        verify(jwtUtil).generateToken("admin@example.com", 2, true);
    }

    @Test
    @DisplayName("login_異常_削除済みユーザーは例外をスロー")
    void login_deletedUser_throwsRuntimeException() {
        DtoAuthRequest req = buildRequest("deleted@example.com", "pass");
        TUserEntity deleted = buildUser(3, "deleted@example.com", 0, 1); // deleteFlg=1
        when(userDetailsService.loadUserByUsername("deleted@example.com")).thenReturn(deleted);

        assertThatThrownBy(() -> authService.login(req))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Invalid credentials");
    }

    @Test
    @DisplayName("login_異常_認証失敗は例外をスロー")
    void login_badCredentials_throwsRuntimeException() {
        DtoAuthRequest req = buildRequest("user@example.com", "wrong");
        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("bad credentials"));

        assertThatThrownBy(() -> authService.login(req))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Invalid credentials");
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private DtoAuthRequest buildRequest(String mail, String password) {
        DtoAuthRequest req = new DtoAuthRequest();
        req.setMailAddress(mail);
        req.setPassword(password);
        return req;
    }

    private TUserEntity buildUser(Integer id, String mail, Integer role, Integer deleteFlg) {
        TUserEntity u = new TUserEntity();
        u.setUserId(id);
        u.setMailAddress(mail);
        u.setRole(role);
        u.setDeleteFlg(deleteFlg);
        return u;
    }
}
