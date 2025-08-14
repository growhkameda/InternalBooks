package com.example.internalbooks.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

/**
 * 管理者専用機能のコントローラー
 */
@Controller
@RequestMapping("/admin")
public class AdminController extends InternalBooksController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    /**
     * 管理者ユーザートップページに遷移
     */
    @GetMapping("/adminusertop")
    public String adminusertop(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            // 管理者権限の検証（継承した共通メソッドを使用）
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            logger.info("★★★★★★★★★★★usertop() にアクセスされました");
            
            return "page/adminusertop";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }
    
    /**
     * 書籍編集ページに遷移
     */
    @GetMapping("/bookediting")
    public String bookediting(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            logger.info("★★★★★★★★★★★bookediting() にアクセスされました");
            
            return "page/bookediting";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }
    
    /**
     * ユーザー確認ページに遷移
     */
    @GetMapping("/userconfir")
    public String UserConfir(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            logger.info("★★★★★★★★★★★UserConfir() にアクセスされました");
            
            return "page/UserConfir";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }
    
    /**
     * ユーザー登録完了ページに遷移
     */
    @GetMapping("/userregistrationcomplete")
    public String UserRegistrationComplete(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            logger.info("★★★★★★★★★★★UserRegistrationComplete() にアクセスされました");
            
            return "page/UserRegistrationComplete";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }
    
    /**
     * 書籍登録確認ページに遷移
     */
    @GetMapping("/bookingconfirmation")
    public String BookingConfirmation(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            logger.info("★★★★★★★★★★★BookingConfirmation() にアクセスされました");
            
            return "page/BookingConfirmation";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }
    
    /**
     * 書籍登録完了ページに遷移
     */
    @GetMapping("/bookingregistrationcomplete")
    public String BookingRegistrationComplete(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            logger.info("★★★★★★★★★★★BookingRegistrationComplete() にアクセスされました");
            
            return "page/BookingRegistrationComplete";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }
    
    /**
     * ユーザー編集完了画面へ遷移
     */
    @GetMapping("/finishuseredit")
    public String finishUserEdit(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            
            return "page/finishUserEdit";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }
    
    /**
     * ユーザー確認画面に遷移
     */
    @GetMapping("/userconfirmation")
    public String userConfirmation(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            
            return "page/userConfirmation";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }
    
    /**
     * ユーザー削除確認ページに遷移
     */
    @GetMapping("/userdeleteconfirmation")
    public String userDeleteConfirmation(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            
            return "page/userDeleteConfirmation";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }
    
    /**
     * ユーザー編集ページに遷移
     */
    @GetMapping("/useredit")
    public String userEdit(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            
            return "page/userEdit";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }
    
    /**
     * ユーザー削除完了ページに遷移
     */
    @GetMapping("/userdeletecomplete")
    public String userDeleteComplete(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            
            return "page/userDeleteComplete";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }
    
    /**
     * 書籍削除確認ページに遷移
     */
    @GetMapping("/bookdeletingconfirmation")
    public String BookDeletingConfirmation(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            
            return "page/BookDeletingConfirmation";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }

    /**
     * ユーザー検索ページに遷移
     */
    @GetMapping("/usersearch")
    public String UserSearch(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            
            return "page/UserSearch";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }
}
