package com.example.internalbooks.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.internalbooks.dto.DtoBookInfo;
import com.example.internalbooks.dto.DtoUserRegistration;
import com.example.internalbooks.entity.TBookEntity;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.service.AuthService;
import com.example.internalbooks.service.TBookService;
import com.example.internalbooks.service.TUserService;
import com.example.internalbooks.utils.JwtUtil;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


/**
 * 管理者専用機能のコントローラー
 */
@Controller
@RequestMapping("/admin")
@SessionAttributes({"userdto, bookdto"})
public class AdminController extends InternalBooksController {
    
    //ロガー
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    //DI用フィールド
    private final TUserService tUserService;
    private final TBookService tBookService;

    //コンストラクタインジェクション    
    public AdminController(JwtUtil jwtUtil, AuthService authService, TBookService tBookService, TUserService tUserService) {
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
            
            model.addAttribute("bookdto", new DtoBookInfo()); //空のDTOを返す
            
            return "page/bookediting";
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
    public String bookdeletingconfirmation(
            @RequestParam("bookId") Integer bookId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            // トークンと管理者権限の検証
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
            
            // 各ユーザーの所属課を取得してモデルに追加する
            List<TUserEntity> userWithDepartmentName = tUserService.getUserDepartmentName();
            
            model.addAttribute("users", userWithDepartmentName);
            model.addAttribute("isAdmin", isAdmin);

            // ログを出力
            logger.info("UserSearchにアクセスされました");
            
            return "page/usersearch";
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
    public String UserConfir(@Valid @ModelAttribute("userDto") DtoUserRegistration userDto, BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes,
            Model model) {
    	
    	if (bindingResult.hasErrors()) {
    		for (FieldError error : bindingResult.getFieldErrors()) {
    			
    			if("Pattern".equals(error.getCode())){
    				
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
        }
        catch (Exception e) {
            return error(redirectAttributes);
        }
    }
    
 // 戻る（入力画面へ戻す）入力画面へ戻った際にセッションが残っていなかった為追加。
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
         // DBに保存した値をDTOを経由して再度取得           
            DtoUserRegistration tuser = new DtoUserRegistration();
            tuser.setUserId(savedUser.getUserIdAsString());
            tuser.setName(savedUser.getName());
            tuser.setMailAddress(savedUser.getMailAddress());
            tuser.setDepartmentId(savedUser.getDepartmentIdAsString());
            tuser.setPassword(savedUser.getPassword());
            
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
    public String BookingConfirmation(@Valid @ModelAttribute("bookdto")DtoBookInfo bookDto, BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes,
             @RequestParam("imagefile")  
    	     MultipartFile file, Model model) {
    	
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
            
            model.addAttribute("bookdto",bookDto);
            
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
    public String BookingRegistrationcomplete(@ModelAttribute("bookdto") DtoBookInfo bookDto,SessionStatus status,HttpSession session, RedirectAttributes redirectAttributes, 
             MultipartFile file, Model model) {
    	
        // トークンと管理者権限の検証
        try {
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);
            
            // DBへ(tilte,catgory,providerId,providercommnet)を保存
            TBookEntity savedBook = tBookService.bookEditing(bookDto);
            // DBに保存した値をDTOWO経由して再度取得           
            DtoBookInfo dbook = new DtoBookInfo();
            dbook.setTitle(savedBook.getTitle());
            dbook.setCategory(savedBook.getCategories());
            dbook.setProviderId(savedBook.getProviderId());
            dbook.setProviderComment(savedBook.getProviderComment());
            
            byte[] imageBytes = (byte[]) session.getAttribute("imageBytes");
            if (imageBytes != null) {
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                model.addAttribute("imagePreview", base64Image);
            }
            
            
            // 取得した情報を表示
            model.addAttribute("dbook",dbook);

            // 完了後に画像をセッションから削除
            session.removeAttribute("imageBytes");
            // セッション破棄（フォームを消す）
            status.setComplete();

            return "page/BookingRegistrationComplete";
        }
        catch (Exception e) {
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
    	}
    	catch (Exception e) {
            return error(redirectAttributes);
    	}
        
    }

    /** 11/03 木俣
     * 書籍削除画面を表示
     * 現在は松永さんが作成したコントローラーを持ってきているので
     * 今後修正があった場合は反映させること(ページ数のロジックのところとかリファクタリング対象)
     * 貸出中の書籍はグレーマスクして選択できないようにしている
     */
    @GetMapping("/bookdeleting")
    public String bookDeleting(
        @RequestParam("category") String category,
        @RequestParam(value = "page", defaultValue = "0") int page,
        HttpSession session,
        Model model,
        RedirectAttributes redirectAttributes) {

        //1ページに表示する本の数
        final int ITEMS_PER_PAGE = 6;

        try {
            // トークンと管理者権限の検証
            boolean isAdmin = validateTokenAndCheckAdmin(session);
            if (!isAdmin) {
                return adminPermissionError(redirectAttributes);
            }
            
            model.addAttribute("isAdmin", isAdmin);

            // カテゴリーに属するすべての書籍情報を取得（松永さんのメソッドと似てるけど違うやつ）
            List<DtoBookInfo> allBooks = tBookService.getBooksByCategoryWithDetails(category);
            
            //取得した本の要素数を取得
            int totalItems = allBooks.size();
            
            //指定した表示画像数と、取得した要素数で必要なページ数を計算
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);

            // ページ範囲を計算
            int fromIndex = page * ITEMS_PER_PAGE;
            int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, totalItems);

            // 表示対象の書籍リストを抽出
            List<DtoBookInfo> pagedBooks = allBooks.subList(fromIndex, toIndex);

            // Viewに渡すモデル属性を設定
            model.addAttribute("bookList", pagedBooks);
            model.addAttribute("category", category);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);

            // セッションにカテゴリーを保存（削除処理で使用）
            session.setAttribute("currentCategory", category);

            return "page/bookdeleting";
    	}
    	catch (Exception e) {
    		// 認証失敗時はログインページにリダイレクト
            return error(redirectAttributes);
        }
    }

    /** 11/03 木俣
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
}
