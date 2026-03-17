package com.example.internalbooks.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.internalbooks.service.TUserService;

import com.example.internalbooks.dto.DtoBookInfo;
import com.example.internalbooks.dto.DtoUserEdit;
import com.example.internalbooks.dto.DtoUserRegistration;
import com.example.internalbooks.entity.MDepartmentEntity;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.common.Const;
import com.example.internalbooks.service.AuthService;
import com.example.internalbooks.service.TBookService;
import com.example.internalbooks.utils.JwtUtil;

/**
 * 管理者専用機能のコントローラー
 */
@Controller
@RequestMapping("/admin")
public class AdminController extends InternalBooksController {

    // ロガー
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);


    // DI用フィールド
    private final TUserService tUserService;
    private final TBookService tBookService;

    // コンストラクタインジェクション
    public AdminController(JwtUtil jwtUtil, AuthService authService, TBookService tBookService,
            TUserService tUserService) {
        super(jwtUtil, authService, tBookService);
        this.tUserService = tUserService;
        this.tBookService = tBookService;
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
        } catch (Exception e) {
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

            model.addAttribute("bookdto", new DtoBookInfo());
            model.addAttribute("activeUsers", tUserService.getActiveUsers());
            model.addAttribute("existingCategories", tBookService.getAllCategories());

            return "page/bookediting";
        } catch (Exception e) {
            return error(redirectAttributes);
        }
    }

    /**
     * ユーザー確認画面に遷移
     */
    @GetMapping("/userconfirmationscreen")
    public String userConfirmationScreen(@RequestParam("userId") Integer userId, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            // 管理者権限の検証
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            // tokenの検証とユーザーIDの取得
            if (!isAdmin)
                return adminPermissionError(redirectAttributes);

            if (userId == null) {
                redirectAttributes.addFlashAttribute("error", "ユーザーIDが指定されていません");
                return "redirect:/admin/usersearch";
            }
            // ユーザーの部門IDから部門名を取得
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

    /*
     * ユーザー情報編集確認ページに遷移
     */
    @PostMapping("/usereditconfirmation")
    public String userEditConfirmation(@Valid @ModelAttribute("userDto") DtoUserEdit userDto,
            BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {

        try {
            // 管理者権限の検証
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            // tokenの検証とユーザーIDの取得
            if (!isAdmin)
                return adminPermissionError(redirectAttributes);

            // エラーがある場合、編集画面へ遷移する
            if (bindingResult.hasErrors()) {
                model.addAttribute("mDepartmentList", tUserService.getAllDepartments());
                model.addAttribute("errorMessage", "入力内容を確認してください。");
                return "page/userEdit";
            }

            // 所属課名のセット
            userDto.setDepartmentName(tUserService.getDepartmentNameById(userDto.getDepartmentIdAsInteger()));
            model.addAttribute("userDto", userDto);
            model.addAttribute("isAdmin", isAdmin);

            return "page/userEditConfirmation";
        } catch (Exception e) {
            logger.error("usereditconfirmationでエラーが発生しました", e);
            return error(redirectAttributes);
        }
    }

    /*
     * ユーザー編集完了ページに遷移
     */
    @PostMapping("/usereditcomplete")
    public String userEditComplete(@ModelAttribute("userDto") DtoUserEdit userDto, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {

        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin)
                return adminPermissionError(redirectAttributes);

            // 編集内容をDBへ上書きする
            tUserService.updateUser(userDto);

            // 完了画面用の所属課名のセット
            userDto.setDepartmentName(tUserService.getDepartmentNameById(userDto.getDepartmentIdAsInteger()));
            model.addAttribute("userDto", userDto);
            return "page/userEditComplete";

        } catch (IllegalArgumentException e) {
            // 更新に失敗した場合、ユーザー編集ページに遷移する
            logger.warn("更新失敗: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            userDto.setDepartmentName(tUserService.getDepartmentNameById(userDto.getDepartmentIdAsInteger()));
            model.addAttribute("userDto", userDto);
            return "page/userEdit";

        } catch (Exception e) {
            logger.error("userupdatecompleteでエラーが発生しました", e);
            return error(redirectAttributes);

        }
    }

    /**
     * ユーザー削除確認ページに遷移
     */
    @GetMapping("/userdeleteconfirmation")
    public String userDeleteConfirmation(@RequestParam("userId") Integer userId, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);

            // tokenの検証とユーザーIDの取得
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }

            // 各ユーザーの所属課を取得してモデルに追加する
            TUserEntity userWithDepartmentName = tUserService.getUserWithDepartmentNameById(userId);
            // 各ユーザーの情報を取得してモデルに追加する
            TUserEntity user = tUserService.getUserById(userId);
            // 所属課
            model.addAttribute("userdepart", userWithDepartmentName);
            // ユーザー情報
            model.addAttribute("users", userWithDepartmentName);
            // ログインユーザー情報を取得してモデルに追加する
            model.addAttribute("isAdmin", isAdmin);

            // ユーザー情報
            model.addAttribute("users", user);
            // ログインユーザー情報を取得してモデルに追加する
            model.addAttribute("isAdmin", isAdmin);

            // ログを出力
            logger.info("userconfirmationscreenにアクセスされました");

            return "page/userDeleteConfirmation";
        } catch (Exception e) {
            return error(redirectAttributes);
        }
    }

    /**
     * ユーザー編集ページに遷移
     */
    @GetMapping("/useredit")
    public String userEdit(@RequestParam("userId") Integer userId, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin)
                return adminPermissionError(redirectAttributes);

            if (userId == null) {
                redirectAttributes.addFlashAttribute("error", "ユーザーIDが指定されていません");
                return "redirect:/admin/usersearch";
            }

            // 編集するためDtoUserEditに詰め替える
            DtoUserEdit userDto = tUserService.getUserEditDtoById(userId);

            model.addAttribute("mDepartmentList", tUserService.getAllDepartments());
            model.addAttribute("userDto", userDto);
            model.addAttribute("isAdmin", isAdmin);

            return "page/userEdit";
        } catch (Exception e) {
            logger.error("userEditでエラーが発生しました", e);
            return error(redirectAttributes);
        }
    }

    /**
     * ユーザー削除完了ページに遷移
     */
    @GetMapping("/userdeletecomplete")
    public String userDeleteComplete(@RequestParam("userId") Integer userId, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            // 管理者権限の検証
            boolean isAdmin = validateTokenAndCheckAdmin(session);

            // tokenの検証とユーザーIDの取得
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }

            tUserService.deleteUser(userId);

            // 各ユーザーの所属課を取得してモデルに追加する
            TUserEntity userWithDepartmentName = tUserService.getUserWithDepartmentNameById(userId);
            // 各ユーザーの情報を取得してモデルに追加する
            TUserEntity user = tUserService.getUserById(userId);

            // 所属課
            model.addAttribute("userdepart", userWithDepartmentName);
            // ユーザー情報
            model.addAttribute("users", user);
            // ログインユーザー情報を取得してモデルに追加する
            model.addAttribute("isAdmin", isAdmin);

            // ユーザー情報
            model.addAttribute("users", user);
            // ログインユーザー情報を取得してモデルに追加する
            model.addAttribute("isAdmin", isAdmin);

            // ログを出力
            logger.info("userdeletecompleteにアクセスされました");

            return "page/userDeleteComplete";
        } catch (Exception e) {
            logger.error("userdeletecompleteでエラーが発生しました", e);
            return error(redirectAttributes);
        }
    }

    /**
     * 書籍削除確認ページに遷移
     */
    @GetMapping("/bookdeletingconfirmation")
    public String bookdeletingconfirmation(@RequestParam("bookId") Integer bookId, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }

            model.addAttribute("isAdmin", isAdmin);

            // bookIdに基づいて書籍情報を取得
            DtoBookInfo bookInfo = tBookService.getBookById(bookId);
            model.addAttribute("bookInfo", bookInfo);

            // セッションからカテゴリーを取得
            String category = (String) session.getAttribute("currentCategory");
            model.addAttribute("category", category);

            return "page/bookdeletingconfirmation";
        } catch (Exception e) {
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

            // 各ユーザーの所属課を取得してモデルに追加する
            List<TUserEntity> userWithDepartmentName = tUserService
                    .getUserDepartmentName((Integer) session.getAttribute("currentUserId"));

            model.addAttribute("users", userWithDepartmentName);
            model.addAttribute("isAdmin", isAdmin);

            // ログを出力
            logger.info("UserSearchにアクセスされました");

            return "page/UserSearch";
        } catch (Exception e) {
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

            model.addAttribute("userDto", new DtoUserRegistration()); // 空のDTOを返す

            model.addAttribute("departments", tUserService.getAllDepartments());

            return "page/UserRegistration";

        } catch (Exception e) {
            return error(redirectAttributes);
        }
    }

    /**
     * ユーザー登録確認画面に遷移
     */
    @PostMapping("/userconfir")
    public String UserConfir(@Valid @ModelAttribute("userDto") DtoUserRegistration userDto, BindingResult bindingResult,
            HttpSession session, RedirectAttributes redirectAttributes,
            Model model) {
    	
        if (bindingResult.hasErrors()) {
        	
        	// バリデーションエラー後もセレクトを表示
            model.addAttribute("departments", tUserService.getAllDepartments());
            
            for (FieldError error : bindingResult.getFieldErrors()) {
                // コンソールにも表示
                System.out.println(error.getField() + ":" + error.getDefaultMessage());
            }

                return "page/UserRegistration";
         }
            
          //重複IDがある場合に表示
         if(tUserService.getUserId(userDto.getUserId()).isPresent()) {
        	bindingResult.rejectValue("userId",null,"このIDは存在します");
             // バリデーションエラー後もセレクトを表示
             model.addAttribute("departments", tUserService.getAllDepartments());
                
                return "page/UserRegistration";
        	}

        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }

            model.addAttribute("isAdmin", isAdmin);

            return "page/UserConfir";
        } catch (Exception e) {
            return error(redirectAttributes);
        }
    }

    // 戻る（入力画面へ戻す）入力画面へ戻った際にセッションが残っていなかった為追加。
    @PostMapping("/back")
    public String UserRegistrationBack(@ModelAttribute("userDto") DtoUserRegistration userDto, HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }

            model.addAttribute("isAdmin", isAdmin);

            model.addAttribute("userDto", userDto);

            model.addAttribute("departments", tUserService.getAllDepartments());

            return "page/UserRegistration";
        } catch (Exception e) {
            return error(redirectAttributes);
        }

    }

    // ユーザー登録完了の送信処理
    @PostMapping("/userregistrationcomplete")
    public String UserRegistrationComplete(@ModelAttribute("userDto") DtoUserRegistration userdto, SessionStatus status,
            HttpSession session, RedirectAttributes redirectAttributes, Model model) {

        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }

            model.addAttribute("isAdmin", isAdmin);

            // Nullチェック
            if (userdto == null) {
                throw new IllegalStateException("DTOがnullです");
            }

            // DBへ(userId,name,mailAddress,password,departmentId)を保存し、登録完了表示用DTOを取得
            DtoUserRegistration tuser = tUserService.userRegistration(userdto);

            // 取得した情報を表示
            model.addAttribute("tuser", tuser);

            status.setComplete();

            return "page/UserRegistrationComplete";

        } catch (Exception e) {
            return error(redirectAttributes);
        }

    }

    /**
     * 書籍登録確認画面へ遷移
     */
    @PostMapping("/bookingconfirmation")
    public String BookingConfirmation(@Valid @ModelAttribute("bookdto") DtoBookInfo bookDto,
            BindingResult bindingResult, @RequestParam("imageFile") MultipartFile file,
            HttpSession session, RedirectAttributes redirectAttributes, Model model) {

        // 書籍画像選択されない時用エラー表示
        // MultipartFileはBeanvalidationに向いていないため下記で選択されていない場合のエラーバリデーション作成
        if (bookDto.getImageFile() == null || bookDto.getImageFile().isEmpty()) {
            bindingResult.rejectValue(
                    "imageFile",
                    "imageFile.empty",
                    "画像を選択してください");
        }

        // その他のエラー表示
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                // コンソールにも表示
                System.out.println(error.getField() + ":" + error.getDefaultMessage());

                return "page/bookediting";
            }
        }

        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }

            model.addAttribute("isAdmin", isAdmin);

            tBookService.tbookconfirm(bookDto);

            // 確認ページプレビュー用に画像バイト列をセッションに保持
            MultipartFile imageFile = bookDto.getImageFile();
            if (imageFile != null && !imageFile.isEmpty()) {
                session.setAttribute("tempImageBytes", imageFile.getBytes());
                session.setAttribute("tempImageContentType",
                        imageFile.getContentType() != null ? imageFile.getContentType() : "image/png");
            }

            System.out.println("imageurl =" + bookDto.getImageUrl());

            model.addAttribute("bookdto", bookDto);

            return "page/BookingConfirmation";

        } catch (Exception e) {
            logger.error("bookingconfirmationでエラーが発生しました", e);
            return error(redirectAttributes);
        }
    }

    /**
     * 書籍登録確認ページ用 一時画像プレビューエンドポイント
     */
    @GetMapping("/temp-image")
    @ResponseBody
    public ResponseEntity<byte[]> getTempImage(HttpSession session) {
        byte[] imageBytes = (byte[]) session.getAttribute("tempImageBytes");
        if (imageBytes == null) {
            return ResponseEntity.notFound().build();
        }
        String contentType = (String) session.getAttribute("tempImageContentType");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(imageBytes);
    }

    /**
     * 書籍登録完了画面へ遷移
     */
    @PostMapping("/bookingregistrationcomplete")
    public String BookingRegistrationcomplete(@ModelAttribute("bookdto") DtoBookInfo bookDto, SessionStatus status,
            HttpSession session, RedirectAttributes redirectAttributes,
            Model model) {

        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }

            model.addAttribute("isAdmin", isAdmin);

            // DBへ(tilte,catgory,providerId,providercommnet)を保存し、登録完了表示用DTOを取得
            DtoBookInfo dbook = tBookService.bookEditing(bookDto);

            // 取得した情報を表示
            model.addAttribute("dbook", dbook);

            // 完了後に画像をセッションから削除
            session.removeAttribute("imageBytes");
            session.removeAttribute("tempImageBytes");
            session.removeAttribute("tempImageContentType");
            // セッション破棄（フォームを消す）
            status.setComplete();

            return "page/BookingRegistrationComplete";
        } catch (Exception e) {
            logger.error("bookingregistrationcompleteでエラーが発生しました", e);
            return error(redirectAttributes);
        }

    }

    /**
     * 削除対象カテゴリーリストを表示
     */
    @GetMapping("/bookdeletingcategories")
    public String bookdeletingcategories(
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session, Model model, RedirectAttributes redirectAttributes) {

        try {
            // トークンと管理者権限の検証
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }

            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("categories", tBookService.getPagedCategories(page, Const.CATEGORIES_PER_PAGE));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", tBookService.getCategoryTotalPages(Const.CATEGORIES_PER_PAGE));

            return "page/bookdeletingcategories";
        } catch (Exception e) {
            return error(redirectAttributes);
        }

    }

    /**
     * 書籍削除画面を表示
     */
    @GetMapping("/bookdeleting")
    public String BookDeleting(
            @RequestParam("category") String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // トークンと管理者権限の検証
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }

            model.addAttribute("isAdmin", isAdmin);

            // Viewに渡すモデル属性を設定
            model.addAttribute("bookList", tBookService.getPagedBooksByCategory(category, page, Const.BOOKS_PER_PAGE));
            model.addAttribute("category", category);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", tBookService.getBooksByCategoryTotalPages(category, Const.BOOKS_PER_PAGE));

            // セッションにカテゴリーを保存（削除処理で使用）
            session.setAttribute("currentCategory", category);

            return "page/bookdeleting";
        } catch (Exception e) {
            // 認証失敗時はログインページにリダイレクト
            return error(redirectAttributes);
        }

    }

    /**
     * 11/03 木俣
     * 書籍削除処理（貸出履歴もカスケード削除）
     */
    @PostMapping("/bookdeleting")
    public String bookDeletingPost(
            @RequestParam("bookId") Integer bookId,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            // トークンと管理者権限の検証
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }

            model.addAttribute("isAdmin", isAdmin);

            // セッションからカテゴリーを取得
            String category = (String) session.getAttribute("currentCategory");

            // 書籍削除処理
            boolean isDeleted = tBookService.deleteBookById(bookId);

            if (isDeleted) {
                redirectAttributes.addFlashAttribute("message", "書籍の削除が完了しました");
            } else {
                redirectAttributes.addFlashAttribute("message", "書籍の削除に失敗しました");
            }

            // 元のカテゴリーページにリダイレクトさせる
            redirectAttributes.addAttribute("category", category);
            return "redirect:/admin/bookdeleting";

        } catch (IllegalStateException e) {
            // 貸出中の書籍削除エラー
            redirectAttributes.addFlashAttribute("message", "エラー: " + e.getMessage());
            return "redirect:/admin/bookdeletingcategories";

        } catch (Exception e) {
            return error(redirectAttributes);
        }

    }

    /*
     * 課一覧リストを取得
     */
    @ModelAttribute("mDepartmentList")
    public List<MDepartmentEntity> getDepartmentList() {
        return tUserService.getAllDepartments();
    }

}