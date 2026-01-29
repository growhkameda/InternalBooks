package com.example.internalbooks.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.internalbooks.dto.DtoBookInfo;
import com.example.internalbooks.dto.DtoUserConfirmationScreen;
import com.example.internalbooks.dto.DtoUserEdit;
import com.example.internalbooks.dto.DtoUserRegistration;
import com.example.internalbooks.entity.MDepartmentEntity;
import com.example.internalbooks.entity.TBookEntity;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.service.AuthService;
import com.example.internalbooks.service.TBookService;
import com.example.internalbooks.service.TUserService;
import com.example.internalbooks.utils.JwtUtil;

/**
 * 管理者専用機能のコントローラー
 */
@Controller
@RequestMapping("/admin")
@SessionAttributes({ "userDto", "bookdto" })
public class AdminController extends InternalBooksController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final TUserService tUserService;
    private final TBookService tBookService;

    public AdminController(JwtUtil jwtUtil, AuthService authService, TBookService tBookService,
            TUserService tUserService) {
        super(jwtUtil, authService, tBookService);
        this.tUserService = tUserService;
        this.tBookService = tBookService;
    }

    /**
     * 管理者ユーザートップページ
     */
    @GetMapping("/adminusertop")
    public String adminusertop(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) return adminPermissionError(redirectAttributes);

            model.addAttribute("isAdmin", isAdmin);
            return "page/adminusertop";
        } catch (Exception e) {
            return error(redirectAttributes);
        }
    }
    /**
     * ユーザー確認画面
     */
    @GetMapping("/userconfirmationscreen")
    public String userConfirmationScreen(@RequestParam("userId") Integer userId, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) return adminPermissionError(redirectAttributes);

            if (userId == null) {
                redirectAttributes.addFlashAttribute("error", "ユーザーIDが指定されていません");
                return "redirect:/admin/usersearch";
            }
            TUserEntity user = tUserService.getUserWithDepartmentNameById(userId);
            model.addAttribute("userDto", user); 
            
            model.addAttribute("isAdmin", isAdmin);

            logger.info("ユーザー確認画面表示: userId={}", userId);
            return "page/userConfirmationScreen";
            
        } catch (Exception e) {
            logger.error("userConfirmationScreenエラー", e);
            return error(redirectAttributes);
        }
    }

    /**
     * ユーザー編集ページ
     */
    @GetMapping("/useredit")
    public String userEdit(@RequestParam("userId") Integer userId, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) return adminPermissionError(redirectAttributes);

            if (userId == null) {
                redirectAttributes.addFlashAttribute("error", "ユーザーIDが指定されていません");
                return "redirect:/admin/usersearch";
            }

            TUserEntity user = tUserService.getUserById(userId);
            user.setDepartmentName(tUserService.getDepartmentNameById(user.getDepartmentId()));
            model.addAttribute("userDto", user);
            model.addAttribute("isAdmin", isAdmin);
            
            return "page/useredit";
        } catch (Exception e) {
            return error(redirectAttributes);
        }
    }

    /**
     * ユーザー編集確認
     */
    @PostMapping("/usereditconfirmation")
    public String userEditConfirmation(@Valid @ModelAttribute("userDto") TUserEntity userDto,
            BindingResult bindingResult, 
            @RequestParam(name = "currentPassword", required = false) String currentPwd,
            @RequestParam(name = "newPassword", required = false) String newPwd, 
            HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {

        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) return adminPermissionError(redirectAttributes);

            if (bindingResult.hasErrors()) {
                userDto.setDepartmentName(tUserService.getDepartmentNameById(userDto.getDepartmentId()));
                model.addAttribute("errorMessage", "入力チェックエラー");
                return "page/useredit";
            }

            // パスワード入力チェック
            if ((StringUtils.hasText(currentPwd) && !StringUtils.hasText(newPwd))
                    || (!StringUtils.hasText(currentPwd) && StringUtils.hasText(newPwd))) {
                userDto.setDepartmentName(tUserService.getDepartmentNameById(userDto.getDepartmentId()));
                model.addAttribute("errorMessage", "変更する場合は両方のパスワードを入力してください");
                return "page/useredit";
            }

            session.setAttribute("currentPwd", currentPwd);
            session.setAttribute("newPwd", newPwd);

            userDto.setDepartmentName(tUserService.getDepartmentNameById(userDto.getDepartmentId()));
            model.addAttribute("userDto", userDto);
            model.addAttribute("isAdmin", isAdmin);

            return "page/usereditconfirmation";
        } catch (Exception e) {
            return error(redirectAttributes);
        }
    }

    /**
     * ユーザー編集完了
     */
    @PostMapping("/usereditcomplete")
    public String userEditComplete(@ModelAttribute("userDto") TUserEntity userDto, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) return adminPermissionError(redirectAttributes);

            String currentPwd = (String) session.getAttribute("currentPwd");
            String newPwd = (String) session.getAttribute("newPwd");

            tUserService.updateUser(userDto, currentPwd, newPwd);
            
            model.addAttribute("userDto", userDto);
            return "page/usereditcomplete";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            userDto.setDepartmentName(tUserService.getDepartmentNameById(userDto.getDepartmentId()));
            return "page/useredit";
        } catch (Exception e) {
            return error(redirectAttributes);
        } finally {
            session.removeAttribute("currentPwd");
            session.removeAttribute("newPwd");
        }
    }

    /**
     * ユーザー削除確認
     */
    @GetMapping("/userdeleteconfirmation")
    public String userDeleteConfirmation(@RequestParam("userId") Integer userId, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) return adminPermissionError(redirectAttributes);

            TUserEntity user = tUserService.getUserWithDepartmentNameById(userId);
            model.addAttribute("users", user);
            model.addAttribute("isAdmin", isAdmin);

            return "page/userdeleteconfirmation";
        } catch (Exception e) {
            return error(redirectAttributes);
        }
    }

    /**
     * ユーザー削除完了
     */
    @GetMapping("/userdeletecomplete")
    public String userDeleteComplete(@RequestParam("userId") Integer userId, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) return adminPermissionError(redirectAttributes);

            TUserEntity user = tUserService.getUserWithDepartmentNameById(userId);
            tUserService.deleteUser(userId);

            model.addAttribute("users", user);
            model.addAttribute("isAdmin", isAdmin);

            return "page/userdeletecomplete";
        } catch (Exception e) {
            return error(redirectAttributes);
        }
    }
    
    @GetMapping("/usersearch")
    public String UserSearch(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) return adminPermissionError(redirectAttributes);

            List<TUserEntity> userWithDepartmentName = tUserService
                    .getUserDepartmentName((Integer) session.getAttribute("currentUserId"));

            model.addAttribute("users", userWithDepartmentName);
            model.addAttribute("isAdmin", isAdmin);
            return "page/UserSearch";
        } catch (Exception e) {
            return error(redirectAttributes);
        }
    }

    @GetMapping("/userregistration")
    public String UserRegistration(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) return adminPermissionError(redirectAttributes);
            model.addAttribute("userDto", new DtoUserRegistration());
            return "page/UserRegistration";
        } catch (Exception e) {
            return error(redirectAttributes);
        }
    }

    @PostMapping("/userconfir")
    public String UserConfir(@Valid @ModelAttribute("userDto") DtoUserRegistration userDto, BindingResult bindingResult,
            HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) return "page/UserRegistration";
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) return adminPermissionError(redirectAttributes);
            return "page/UserConfir";
        } catch (Exception e) {
            return error(redirectAttributes);
        }
    }

    @PostMapping("/userregistrationcomplete")
    public String UserRegistrationComplete(@ModelAttribute("userDto") DtoUserRegistration userdto, SessionStatus status,
            HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) return adminPermissionError(redirectAttributes);

            TUserEntity savedUser = tUserService.userRegistration(userdto);
            model.addAttribute("tuser", savedUser);
            status.setComplete();
            return "page/UserRegistrationComplete";
        } catch (Exception e) {
            return error(redirectAttributes);
        }
    }

    @ModelAttribute("mDepartmentList")
    public List<MDepartmentEntity> getDepartmentList() {
        return tUserService.getAllDepartments(); 
    }
    
    // --- 書籍関連メソッド（変更なしのため省略して維持可能ですが、全体を上書きしてください） ---
    @GetMapping("/bookediting")
    public String bookediting(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) return adminPermissionError(redirectAttributes);
            model.addAttribute("bookdto", new DtoBookInfo());
            return "page/bookediting";
        } catch (Exception e) {
            return error(redirectAttributes);
        }
    }
}