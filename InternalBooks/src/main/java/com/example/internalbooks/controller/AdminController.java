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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.internalbooks.service.TUserService;

import com.example.internalbooks.dto.DtoBookInfo;
import com.example.internalbooks.dto.DtoUserRegistration;
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

            model.addAttribute("bookdto", new DtoBookInfo()); // 空のDTOを返す

            return "page/bookediting";
        } catch (Exception e) {
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
            model.addAttribute("users", user);
            // ログインユーザー情報を取得してモデルに追加する
            model.addAttribute("isAdmin", isAdmin);

            // ログを出力
            logger.info("userconfirmationscreenにアクセスされました");

            return "page/userconfirmationscreen";
        } catch (Exception e) {
            logger.error("userconfirmationscreenでエラーが発生しました", e);
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

            return "page/userdeleteconfirmation";
        } catch (Exception e) {
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
        } catch (Exception e) {
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

            return "page/userdeletecomplete";
        } catch (Exception e) {
            logger.error("userdeletecompleteでエラーが発生しました", e);
            return error(redirectAttributes);
        }
    }

    /**
     * 書籍削除確認ページに遷移
     */
    @GetMapping("/bookdeletingconfirmation")
    public String bookdeletingconfirmation(@RequestParam("bookid") Integer bookid, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }

            model.addAttribute("isAdmin", isAdmin);

            // bookidに基づいて書籍情報を取得
            DtoBookInfo bookInfo = tBookService.getBookById(bookid);
            model.addAttribute("bookInfo", bookInfo);

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
            for (FieldError error : bindingResult.getFieldErrors()) {

                if ("Pattern".equals(error.getCode())) {

                    // コンソールにも表示
                }
                System.out.println(error.getField() + ":" + error.getDefaultMessage());

                return "page/UserRegistration";
            }

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
                //Nullチェック
                if(userdto == null) {
            	   throw new IllegalStateException("DTOがnullです");
               }
            try {
                //所属課をIDへ変換
                tUserService.userDepartmentId(userdto);
                                                
                // DBへ(userId,name,mailAddress,password,departmentId)を保存
                TUserEntity savedUser = tUserService.userRegistration(userdto);
                // DBに保存した値をDTOを経由して再度取得
                DtoUserRegistration tuser = new DtoUserRegistration();
                tuser.setUserId(savedUser.getUserIdAsString());
                tuser.setName(savedUser.getName());
                tuser.setMailAddress(savedUser.getMailAddress());
                //所属課はStringで表示させるためuserdtoからそのままセットする。
                tuser.setDepartmentId(userdto.getDepartmentId());
                tuser.setPassword(savedUser.getPassword());

                // 取得した情報を表示
                model.addAttribute("tuser",tuser);
               //入力フォームより削除
               status.setComplete();

             return "page/UserRegistrationComplete";
            
            } catch (Exception e) {
        		return error(redirectAttributes);
            }
    	}
    	catch (Exception e) {
    		return error(redirectAttributes);
    	}
    }
    /**
     * 書籍登録確認画面へ遷移
     */
    @PostMapping("/bookingconfirmation")
    public String BookingConfirmation(@Valid @ModelAttribute("bookdto")DtoBookInfo bookDto, BindingResult bindingResult, @RequestParam("imageFile")MultipartFile file,HttpSession session, RedirectAttributes redirectAttributes,
    	     Model model) {
    	
        //MultipartFileはBeanvalidationに向いていないため下記で選択されていない場合のエラーバリデーション作成
    	if (bookDto.getImageFile() == null || bookDto.getImageFile().isEmpty()) {
		    bindingResult.rejectValue(
		        "imageFile",
		        "imageFile.empty",
		        "画像を選択してください");
		}
    	 //その他のエラー表示
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
      
            model.addAttribute("bookdto", bookDto);

            return "page/BookingConfirmation";

        } catch (Exception e) {
            return error(redirectAttributes);
        }

    }
    
    
    // 戻る（入力画面へ戻す）入力画面へ戻った際にセッションが残っていなかった為追加。
    @PostMapping("/dbback")
    public String bookeditingBack(@ModelAttribute("bookdto")DtoBookInfo bookDto, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
                               
            model.addAttribute("isAdmin", isAdmin);
            
            model.addAttribute("bookdto",bookDto);
                   
            return "page/bookediting";
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }

    /**
     * 書籍登録完了画面へ遷移
     */
    @PostMapping("/bookingregistrationcomplete")
    public String BookingRegistrationcomplete(@ModelAttribute("bookdto") DtoBookInfo bookDto,SessionStatus status,HttpSession session, RedirectAttributes redirectAttributes, 
    		Model model) {
    	
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }

            model.addAttribute("isAdmin", isAdmin);
       
            // DBへ(tilte,catgory,providerId,providercommnet)を保存
            TBookEntity savedBook = tBookService.bookEditing(bookDto);
            // DBに保存した値をDTOを経由して再度取得
            DtoBookInfo dbook = new DtoBookInfo();
            
         // DBに保存した値をDTOWO経由して再度取得           
            dbook.setTitle(savedBook.getTitle());
            dbook.setProviderId(savedBook.getProviderName());
            dbook.setCategory(savedBook.getCategories());
            dbook.setProviderComment(savedBook.getProviderComment());

            // 取得した情報を表示
            model.addAttribute("dbook",dbook);
            // セッション破棄（フォームを消す）
            status.setComplete();

            return "page/BookingRegistrationComplete";
        } catch (Exception e) {
            return error(redirectAttributes);
        }

    }

    /**
     * 削除対象カテゴリーリストを表示
     */
    @GetMapping("/bookdeletingcategories")
    public String bookdeletingcategories(HttpSession session, Model model, RedirectAttributes redirectAttributes) {

        try {
            // トークンと管理者権限の検証
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }

            model.addAttribute("isAdmin", isAdmin);

            // カテゴリーリストを取得
            List<String> categoryList = tBookService.getAllCategories();

            model.addAttribute("categories", categoryList);

            return "page/bookdeletingcategories";
        } catch (Exception e) {
            return error(redirectAttributes);
        }

    }

    /**
     * 書籍削除画面を表示
     */
    @GetMapping("/bookdeleting")
    public String BookDeleting(@RequestParam("category") String category, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // トークンと管理者権限の検証
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }

            model.addAttribute("isAdmin", isAdmin);

            // 対象カテゴリーのbookIdを取得してbookIdListに格納
            List<Integer> bookIdList = tBookService.getCategoriesdetail(category);

            model.addAttribute("category", category);
            model.addAttribute("bookIdList", bookIdList);

            return "page/bookdeleting";
        } catch (Exception e) {
            // 認証失敗時はログインページにリダイレクト
            return error(redirectAttributes);
        }

    }

}
