package com.example.internalbooks.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;

import com.example.internalbooks.service.TUserService;
import com.example.internalbooks.dto.DtoUserRegistration;
import com.example.internalbooks.entity.TUserEntity;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.example.internalbooks.utils.JwtUtil;
import com.example.internalbooks.service.AuthService;
import com.example.internalbooks.service.TBookService;


/**
 * 管理者専用機能のコントローラー
 */
@Controller
@RequestMapping("/admin")
@SessionAttributes({"userdto, title,categories,providerId,providerComment"})
public class AdminController extends InternalBooksController {
    
    //ロガー
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    //DI用フィールド
    private final TUserService tUserService;

    //コンストラクタインジェクション    
    public AdminController(JwtUtil jwtUtil, AuthService authService, TBookService tBookService, TUserService tUserService) {
        super(jwtUtil, authService, tBookService);
        this.tUserService = tUserService;
    }

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
            logger.info("adminusertop() にアクセスされました");
            
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
            logger.info("bookediting() にアクセスされました");
            
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
            logger.info("UserConfir() にアクセスされました");
            
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
            logger.info("UserRegistrationComplete() にアクセスされました");
            
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
            logger.info("BookingConfirmation() にアクセスされました");
            
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
            logger.info("BookingRegistrationComplete() にアクセスされました");
            
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
            
            return "page/finishuseredit";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }
    
    /**
     * ユーザー確認画面に遷移
     */
    @GetMapping("/userconfirmationscreen")
    public String userConfirmationScreen(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            
            return "page/UserConfirmationScreen";
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
            
            return "page/useredit";
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
            
            return "page/userdeletecomplete";
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
            
            return "page/bookdeletingconfirmation";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }

    /**
     * ユーザー検索ページに遷移
     * map処理はTUserServiceで行うべきなのでそのうち修正する(木俣)
     */
    @GetMapping("/usersearch")
    public String UserSearch(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            // 管理者権限の検証
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            // 全ユーザー情報を取得
            List<TUserEntity> users = tUserService.getAllUsers();
            
            // 各ユーザーの所属課を取得してモデルに追加する
            Map<Integer, String> departmentNames = new HashMap<>();
            for (TUserEntity user : users) {
                String departmentName = tUserService.getDepartmentNameById(user.getDepartmentId());
                departmentNames.put(user.getUserId(), departmentName);
            }
            
            model.addAttribute("users", users);
            model.addAttribute("departmentNames", departmentNames);
            model.addAttribute("isAdmin", isAdmin);

            // ログを出力
            logger.info("UserSearchにアクセスされました");
            
            return "page/UserSearch";
        }
        catch (Exception e) {
            logger.error("UserSearchでエラーが発生しました", e);
            return error(redirectAttributes);
        }
    }

    /**
     * ユーザー登録画面へ遷移
     */
    @GetMapping("/userregistration")
    public String UserRegistration(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
           // 管理者権限の検証
           boolean isAdmin = validateTokenAndCheckAdmin(session);
           if (!isAdmin) {
               return adminPermissionError(redirectAttributes);
           }
            
            model.addAttribute("isAdmin", isAdmin);
            
            model.addAttribute("userDto", new DtoUserRegistration()); //空のDTOを返す

            return "page/UserRegistration"; 
            
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }

    /**
     * ユーザー登録確認画面に遷移
     */
    @PostMapping("/userconfir")
    public String UserConfir(@ModelAttribute("userDto") DtoUserRegistration userDto, HttpSession session, RedirectAttributes redirectAttributes,
            Model model) {
    	
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            
            return "page/UserConfir";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }
    
 // 戻る（入力画面へ戻す）
    @PostMapping("/back")
    public String UserRegistrationBack(@ModelAttribute("userDto")DtoUserRegistration userDto, HttpSession session, RedirectAttributes redirectAttributes,
            Model model) {
    	
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            
            model.addAttribute("userDto", userDto);
            
            return "page/UserRegistration";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
   
    }
       
 // ユーザー登録完了の送信処理
    @PostMapping("/userregistrationcomplete")
    public String UserRegistrationComplete(@ModelAttribute("userDto") DtoUserRegistration userdto, SessionStatus status, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
    	
    	 // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            
         // DBへ(userId,name,mailAddress,password,departmentId)を保存
            TUserEntity savedUser = tUserService.userRegistration(userdto);
         // DBに保存した値を再度取得           
            TUserEntity tuser = tUserService.getUserById(savedUser.getUserId());
            
            // 取得した情報を表示
            model.addAttribute("tuser",tuser);
    	
            status.setComplete();

            return "page/UserRegistrationComplete"; 
        
    	}
    	catch (Exception e) {
    		return error(redirectAttributes);
    	}
        
    }

    /**
     * 書籍登録確認画面へ遷移
     */
    @PostMapping("/bookingconfirmation")
    public String BookingConfirmation(HttpSession session, RedirectAttributes redirectAttributes,
    	     @RequestParam ("booktitle") String title,
             @RequestParam String categories,
             @RequestParam String providerId,
             @RequestParam String providerComment,
             @RequestParam("imagefile")  
    	     MultipartFile file, Model model) {
    	
    	// トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            
            model.addAttribute("booktitle",title);
            model.addAttribute("categories",categories);
            model.addAttribute("providerId",providerId);
            model.addAttribute("providerComment",providerComment);
            
            if (!file.isEmpty()) {
                try {
                    byte[] imageBytes = file.getBytes();
                    session.setAttribute("imageBytes", imageBytes);

                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    model.addAttribute("imagePreview", base64Image);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return "page/BookingConfirmation"; 
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
        
    }

    /**
     * 書籍登録完了画面へ遷移
     */
    @PostMapping("/bookingregistrationcomplete")
    public String BookingRegistrationcomplete(SessionStatus status,HttpSession session, RedirectAttributes redirectAttributes, 
    		@RequestParam ("booktitle") String title,
            @RequestParam String categories,
            @RequestParam String providerId,
            @RequestParam String providerComment,
   	        MultipartFile file, Model model) {
    	
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            
            model.addAttribute("booktitle",title);
            model.addAttribute("categories",categories);
            model.addAttribute("providerId",providerId);
            model.addAttribute("providerComment",providerComment);
            
            byte[] imageBytes = (byte[]) session.getAttribute("imageBytes");
            if (imageBytes != null) {
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                model.addAttribute("imagePreview", base64Image);
            }

            // 完了後に画像をセッションから削除
            session.removeAttribute("imageBytes");
         // セッション破棄（フォームを消す）
            session.removeAttribute("bookingregistrationcomplete");

            return "page/BookingRegistrationComplete";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
        
    }

}
