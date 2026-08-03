package com.example.internalbooks.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.internalbooks.common.Const;
import com.example.internalbooks.dto.DtoAuthRequest;
import com.example.internalbooks.dto.DtoBookHistory;
import com.example.internalbooks.dto.DtoBookHistoryRegistration;
import com.example.internalbooks.dto.DtoBookInfo;
import com.example.internalbooks.dto.DtoChangePassword;
import com.example.internalbooks.entity.TLendingHistoryEntity;
import com.example.internalbooks.exception.AuthenticationFailedException;
import com.example.internalbooks.service.AuthService;
import com.example.internalbooks.service.TBookService;
import com.example.internalbooks.service.TLendingHistoryService;
import com.example.internalbooks.service.TUserService;
import com.example.internalbooks.utils.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class InternalBooksController {

    // ロガー
    private static final Logger logger = LoggerFactory.getLogger(InternalBooksController.class);
    // DI用フィールド
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final TBookService tBookService;
	private final TUserService tUserService;

    // コンストラクタインジェクション
    public InternalBooksController(JwtUtil jwtUtil, AuthService authService, TBookService tBookService, TUserService tUserService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
        this.tBookService = tBookService;
        this.tUserService = tUserService;
    }

    @Autowired
    private TLendingHistoryService lendingHistoryService;

    // 画面遷移用グローバル変数 カテゴリー一覧：1 QRコード：2 貸出中書籍：3
    Integer screenFlag;

    /**
     * トップページとしてloginを設定
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/page/login";
    }

    /**
     * ログアウト処理
     */
    @GetMapping("/action/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("infoMessage", "ログアウトしました。");
        return "redirect:/page/login";
    }

    /**
     * ログインページに遷移
     */
    @GetMapping("/page/login")
    public String Login(Model model) {
    	model.addAttribute("authDto", new DtoAuthRequest()); // 空のDTOを返す
        return "page/login";
    }

	/**
	 * ログイン処理
	 */
	@PostMapping("/action/login")
	public String login(@Valid @ModelAttribute("authDto") DtoAuthRequest authDto, BindingResult bindingResult,
			HttpSession session, RedirectAttributes redirectAttributes, Model model) {

		// 入力バリデーションエラーがある場合ログイン画面へ遷移
		if (bindingResult.hasErrors()) {
			model.addAttribute("errorMessage", "入力内容を確認してください。");
			return "page/login";

		}

		try {
			// ログイン処理を実行し成功したらtokenを設定
			// 認証が失敗するとエラーがなげられるためCatchにひっかかる
			String token = authService.login(authDto);
			log.info("ログイン成功: メールアドレス = {}", authDto.getMailAddress());
			// セッションにtokenを設定
			session.setAttribute("token", token);
			return "redirect:/page/top";

		} catch (AuthenticationFailedException e) {
			// 認証失敗した場合
			log.warn("ログイン失敗（認証エラー）: {}", e.getMessage());
			model.addAttribute("errorMessage", e.getMessage());
			return "page/login"; // ログイン画面に戻す

		} catch (Exception e) {
			// それ以外の予期せぬエラー（DB接続不可など）
			log.error("ログイン失敗（システムエラー）: メールアドレス = {}", authDto.getMailAddress(), e);
			return error(redirectAttributes);

		}
	}

    /**
     * TOPページに遷移
     */
    @GetMapping("/page/top")
    public String top(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            // トークンの検証
            validateTokenAndGetUserId(session);

            boolean isAdmin = validateTokenAndCheckAdmin(session);
            model.addAttribute("isAdmin", isAdmin);

            return "page/top";
        } catch (Exception e) {
            return error(redirectAttributes);
        }

    }

    /**
     * カテゴリーリストを表示
     */
    @GetMapping("/page/categories")
    public String categories(
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            // トークンの検証
            validateTokenAndGetUserId(session);

            model.addAttribute("categories", tBookService.getPagedCategories(page, Const.CATEGORIES_PER_PAGE));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", tBookService.getCategoryTotalPages(Const.CATEGORIES_PER_PAGE));

            return "page/categories";
        } catch (Exception e) {
            return error(redirectAttributes);
        }

    }

    /**
     * 貸出中書籍ページに遷移
     */
    @GetMapping("/page/checkedout")
    public String checkedout(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            // tokenの検証とユーザーIDの取得
            Integer userId = validateTokenAndGetUserId(session);

            // 現在のユーザーの貸出中書籍を取得（返却予定日も含む）
            List<DtoBookInfo> checkedOutBooks = tBookService.getCheckedOutBooksByUserId(userId);

            model.addAttribute("checkedOutBooks", checkedOutBooks);

            // フラグ 貸出中書籍：3
            screenFlag = 3;

            // 貸出中書籍からの遷移フラグをセッションに設定
            session.setAttribute("screenFlag", screenFlag);
            //古川追記：ブラウザバックによる二重返却を防止するフラグを設定
            session.setAttribute(Const. RETURN_COMPLETED_FLAG,true);

            return "page/checkedout";
        } catch (Exception e) {
            return error(redirectAttributes);
        }

    }

    /**
     * QRコードサーチページを表示
     */
    @GetMapping("/page/qrsearch")
    public String qrScanner(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            // JWT認証トークンの検証
            validateTokenAndGetUserId(session);

            // フラグ QRコード：2
            screenFlag = 2;

            // QRコードからの遷移フラグをセッションに設定
            session.setAttribute("screenFlag", screenFlag);

            return "page/qrsearch";
        } catch (Exception e) {
            // 認証失敗時はログインページにリダイレクト
            return error(redirectAttributes);
        }

    }

    /**
     * カテゴリー詳細ページに遷移
     */
    @GetMapping("/page/categories_detail")
    public String categories_detail(
            @RequestParam("category") String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // ユーザー認証（共通処理）
            validateTokenAndGetUserId(session);

            // フラグ カテゴリー一覧：1
            screenFlag = 1;

            // Viewに渡すモデル属性を設定
            model.addAttribute("bookList", tBookService.getPagedBooksByCategory(category, page, Const.BOOKS_PER_PAGE));
            model.addAttribute("category", category);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", tBookService.getBooksByCategoryTotalPages(category, Const.BOOKS_PER_PAGE));

            // カテゴリー一覧からの遷移フラグをセッションに設定
            session.setAttribute("screenFlag", screenFlag);

            return "page/categories_detail";

        } catch (Exception e) {
            return error(redirectAttributes);
        }
    }

    /**
     * 検索結果詳細ページに遷移
     * (session情報詰め込みすぎた…いつかServiceに移行しないといけない(木俣))
     */
    @RequestMapping("/page/searchresult")
    public String searchResult(
            @ModelAttribute("tlend") DtoBookHistoryRegistration dtlend,
            BindingResult bindingResult,
            @RequestParam(name = "bookId", required = false) Integer bookId,
            @RequestParam(name = "qrData", required = false) String qrData,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // JWT認証トークンの検証（共通メソッド）
            validateTokenAndGetUserId(session);

            model.addAttribute("bookdto", new DtoBookHistoryRegistration());

            // フラグで画面管理
            screenFlag = (Integer) session.getAttribute("screenFlag");
            // 貸出/返却ボタンの表示・非表示
            boolean showButton;
            // コメントの表示・非表示
            boolean showComment;

            // 遷移元によってボタン・コメントの表示を切り替え
            switch (screenFlag) {
                // カテゴリー一覧からの遷移
                case 1:
                    showButton = false;
                    showComment = false;
                    model.addAttribute("showButton", showButton);
                    model.addAttribute("showComment", showComment);
                    break;
                // 借りるボタンからの遷移
                case 2:
                    showButton = true;
                    showComment = false;
                    model.addAttribute("showButton", showButton);
                    model.addAttribute("showComment", showComment);
                    break;
                // 返すボタンからの遷移
                case 3:
                    showButton = true;
                    showComment = true;
                    model.addAttribute("showButton", showButton);
                    model.addAttribute("showComment", showComment);
                    break;
                default:
                    break;
            }
            model.addAttribute("screenFlag", screenFlag);

            // 書籍検索処理をServiceで処理
            DtoBookInfo book = tBookService.processBookSearchRequest(bookId, qrData);
            if (book == null) {
                // QR起点のエラーは qrsearch に戻して再スキャンを促す
                if (qrData != null) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "QRコードを読み取れませんでした。書籍のQRコードを枠内に合わせて、もう一度スキャンしてください。");
                    return "redirect:/page/qrsearch";
                }
                redirectAttributes.addFlashAttribute("error", "書籍が取得できませんでした");
                return "redirect:/page/top";
            }
            
            // 貸出中書籍にはアクセスできないように制御
            Integer loginUserId = validateTokenAndGetUserId(session);
            if ("貸出中".equals(book.getStatus())) {
	            if (!tBookService.isBookBorrowedByUser(book.getBookId(), loginUserId)) {
					redirectAttributes.addFlashAttribute("error", "この書籍は貸出中のためアクセスできません");
					return "redirect:/page/top";
				}
            }
            model.addAttribute("book", book);
            model.addAttribute("categories", book.getCategories());

        	// 書籍履歴を取得
        	List<DtoBookHistory> dtoBookHistory;

            if (bookId == null && qrData != null) {
                // QRコードから遷移した場合
                Integer qrId = Integer.parseInt(qrData);
                dtoBookHistory = lendingHistoryService.getHistoryByBookId(qrId);
            } else {
                // 一覧から遷移した場合
                dtoBookHistory = lendingHistoryService.getHistoryByBookId(bookId);
            }
            model.addAttribute("bookHistoryList", dtoBookHistory);
            
            // 書籍感想有無の判定
            boolean hasReviewHistory = true;
            // 1件でも感想が記載されていればfalseを渡し感想を表示する
            if(dtoBookHistory.stream().anyMatch(h -> h.getReview() != null)) {
            	hasReviewHistory = false;
            }
            model.addAttribute("hasReviewHistory", hasReviewHistory);

            if (bookId == null && qrData == null) {
                redirectAttributes.addFlashAttribute("error", "書籍IDが取得できませんでした");
                return "redirect:/page/top";
            }

            return "page/SearchResult";

        } catch (AuthenticationFailedException e) {
            // 認証関連の例外は既存通りログイン画面へ
            logger.warn("検索結果詳細ページで認証エラーが発生しました: {}", e.getMessage());
            return error(redirectAttributes);
        } catch (Exception e) {
            logger.error("検索結果詳細ページでエラーが発生しました", e);
            // QR起点（数値変換失敗・予期しない例外など）の場合は qrsearch に戻す
            if (qrData != null) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "QRコードを読み取れませんでした。書籍のQRコードを枠内に合わせて、もう一度スキャンしてください。");
                return "redirect:/page/qrsearch";
            }
            return error(redirectAttributes);
        }
    }

    @PostMapping("/page/LendingCompleted")
    public String searchResultLend(
            @ModelAttribute("tlend") DtoBookHistoryRegistration dtlend,
            // @RequestParam("bookId") Integer bookId,
            @RequestParam(name = "qrData", required = false) String qrData,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // torkenの検証
            String token = (String) session.getAttribute("token");
            Integer bookId;
            Integer userId = jwtUtil.extractUserId(token);
            if (qrData != null) {
                // QRコードで読み取った場合
                bookId = Integer.parseInt(qrData);
            } else {
                // 一覧画面から遷移した場合
                bookId = dtlend.getBookId();
            }

            dtlend.setBookId(bookId);
            dtlend.setUserId(userId);

            boolean isAdmin = jwtUtil.extractIsAdmin(token);
            model.addAttribute("isAdmin", isAdmin);

            lendingHistoryService.rentalCompleted(dtlend);

            DtoBookInfo bookInfo = tBookService.getBookById(bookId);
            model.addAttribute("book", bookInfo);
            model.addAttribute("categories", bookInfo.getCategories());

            // 書籍履歴を取得
            List<DtoBookHistory> dtoBookHistory;

            if (bookId == null) {
                // QRコードから遷移した場合
                Integer qrId = Integer.parseInt(qrData);
                dtoBookHistory = lendingHistoryService.getHistoryByBookId(qrId);
            } else {
                // 一覧から遷移した場合
                dtoBookHistory = lendingHistoryService.getHistoryByBookId(bookId);
            }
            model.addAttribute("bookHistoryList", dtoBookHistory);

            DtoBookHistory latestHistory = dtoBookHistory.isEmpty() ? null : dtoBookHistory.get(0);
            model.addAttribute("bookHistory", latestHistory);

            if (bookId == null) {
                redirectAttributes.addFlashAttribute("error", "書籍IDが取得できませんでした");
                return "redirect:/page/top";
            }

            redirectAttributes.addAttribute("bookId", bookId);

        } catch (Exception e) {
            return error(redirectAttributes);
        }

        return "redirect:/page/LendingCompleted";
    }

    @PostMapping("/page/ReturnCompleted")
    public String searchResultReturn(
    		@Valid @ModelAttribute("tlend") DtoBookHistoryRegistration dtlend,
    		BindingResult bindingResult,
            @RequestParam("bookId") Integer bookId,
            @RequestParam(name = "qrData", required = false) String qrData,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

    	
        try {	
        	
        	//古川追記：ブラウザバック後の再送信などで同じ返却処理が再実行されることを防ぐ
        	// 二重返却防止処理
        	if (session.getAttribute(Const. RETURN_COMPLETED_FLAG) == null) {
        	    redirectAttributes.addFlashAttribute("errorMessage","この書籍はすでに返却処理されています。");
        	    return "redirect:/page/top";
        	}
        	
            // torkenの検証
            String token = (String) session.getAttribute("token");
            Integer userId = jwtUtil.extractUserId(token);
            if (qrData != null) {
                // QRコードで読み取った場合
                bookId = Integer.parseInt(qrData);
            } else {
                // 一覧画面から遷移した場合
                bookId = dtlend.getBookId();
            }

            dtlend.setBookId(bookId);
            dtlend.setUserId(userId);

            boolean isAdmin = jwtUtil.extractIsAdmin(token);
            model.addAttribute("isAdmin", isAdmin);
            
            boolean hasReviewHistory = true;
            
            if (bindingResult.hasErrors()) {
            	model.addAttribute("tlend", dtlend);
                model.addAttribute(
                    "org.springframework.validation.BindingResult.tlend",
                    bindingResult
                );
                model.addAttribute("bookId", dtlend.getBookId());
                
                model.addAttribute("showComment", true);
                model.addAttribute("showButton", true);
                model.addAttribute("screenFlag", 3);
                
                DtoBookInfo bookInfo = tBookService.getBookById(bookId);
                model.addAttribute("book", bookInfo);
                model.addAttribute("categories", bookInfo.getCategories());
                
                List<DtoBookHistory> history = lendingHistoryService.getHistoryByBookId(bookId);
                model.addAttribute("bookHistoryList", history);
                
                if(history.stream().anyMatch(h -> h.getReview() != null)) {
                	hasReviewHistory = false;
                }
                model.addAttribute("hasReviewHistory", hasReviewHistory);
                
                return "page/SearchResult";
            }

            // DBへ(userId,name,mailAddress,password,departmentId)を保存
            TLendingHistoryEntity savedLend = lendingHistoryService.returnCompleted(dtlend);
            

            
            
            // DBに保存した値をDTOを経由して再度取得
            DtoBookHistoryRegistration tlend = new DtoBookHistoryRegistration();
            tlend.setBookId(savedLend.getBookId());
            tlend.setLendingDate(savedLend.getLendingDate());
            tlend.setScheduledReturnDate(savedLend.getScheduledReturnDate());
            tlend.setReturnDate(savedLend.getReturnDate());
            tlend.setUserId(savedLend.getUserId());
            tlend.setReview(savedLend.getReview());

            DtoBookInfo bookInfo = tBookService.getBookById(bookId);

            // 取得した情報を表示
            model.addAttribute("book", bookInfo);

            // 書籍履歴を取得
            List<DtoBookHistory> dtoBookHistory;

            if (bookId == null) {
                // QRコードから遷移した場合
                Integer qrId = Integer.parseInt(qrData);
                dtoBookHistory = lendingHistoryService.getHistoryByBookId(qrId);
            } else {
                // 一覧から遷移した場合
                dtoBookHistory = lendingHistoryService.getHistoryByBookId(bookId);
            }

            model.addAttribute("bookHistoryList", dtoBookHistory);

            DtoBookHistory latestHistory = dtoBookHistory.isEmpty() ? null : dtoBookHistory.get(0);
            model.addAttribute("bookHistory", latestHistory);

            if (bookId == null) {
                redirectAttributes.addFlashAttribute("error", "書籍IDが取得できませんでした");
                return "redirect:/page/top";
            }

            redirectAttributes.addAttribute("bookId", bookId);
            
            //古川追記：ブラウザバックによる二重返却を防止するフラグを削除
            session.removeAttribute(Const. RETURN_COMPLETED_FLAG);
            
            return "redirect:/page/ReturnCompleted";
        } catch (Exception e) {
            return error(redirectAttributes);
        }

    }

    /**
     * 貸出完了ページに遷移
     */
    @GetMapping("/page/LendingCompleted")
    public String LendingCompleted(
            @RequestParam("bookId") Integer bookId,
            @RequestParam(name = "qrData", required = false) String qrData,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // トークンの検証（共通メソッド）
            validateTokenAndGetUserId(session);

            // 書籍検索処理をServiceで処理
            DtoBookInfo book = tBookService.processBookSearchRequest(bookId, qrData);
            model.addAttribute("book", book);
            model.addAttribute("categories", book.getCategories());

            // 書籍履歴を取得
            List<DtoBookHistory> dtoBookHistory;

            if (bookId == null && qrData != null) {
                // QRコードから遷移した場合
                Integer qrId = Integer.parseInt(qrData);
                dtoBookHistory = lendingHistoryService.getHistoryByBookId(qrId);
            } else {
                // 一覧から遷移した場合（基本はこちら）
                dtoBookHistory = lendingHistoryService.getHistoryByBookId(bookId);
            }

            model.addAttribute("bookHistoryList", dtoBookHistory);

            DtoBookHistory latestHistory = dtoBookHistory.isEmpty() ? null : dtoBookHistory.get(0);
            model.addAttribute("bookHistory", latestHistory);
            
            // 書籍感想有無の判定
            boolean hasReviewHistory = true;
            // 貸出完了画面のため、1件でも感想が記載されていればfalseを渡し感想を表示する
            if(dtoBookHistory.stream().anyMatch(h -> h.getReview() != null)) {
            	hasReviewHistory = false;
            }
            model.addAttribute("hasReviewHistory", hasReviewHistory);

            if (bookId == null) {
                redirectAttributes.addFlashAttribute("error", "書籍IDが取得できませんでした");
                return "redirect:/page/top";
            }

            return "page/LendingCompleted";
        } catch (Exception e) {
            return error(redirectAttributes);
        }

    }

    /**
     * 返却完了ページに遷移
     */
    @GetMapping("/page/ReturnCompleted")
    public String ReturnCompleted(
            @RequestParam("bookId") Integer bookId,
            @RequestParam(name = "qrData", required = false) String qrData,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // トークンの検証（共通メソッド）
            validateTokenAndGetUserId(session);

            // 書籍検索処理をServiceで処理
            DtoBookInfo book = tBookService.processBookSearchRequest(bookId, qrData);
            model.addAttribute("book", book);
            model.addAttribute("categories", book.getCategories());

            // 書籍履歴を取得
            List<DtoBookHistory> dtoBookHistory;

            if (bookId == null) {
                // QRコードから遷移した場合
                Integer qrId = Integer.parseInt(qrData);
                dtoBookHistory = lendingHistoryService.getHistoryByBookId(qrId);
            } else {
                // 一覧から遷移した場合
                dtoBookHistory = lendingHistoryService.getHistoryByBookId(bookId);
            }

            model.addAttribute("bookHistoryList", dtoBookHistory);

            DtoBookHistory latestHistory = dtoBookHistory.isEmpty() ? null : dtoBookHistory.get(0);
            model.addAttribute("bookHistory", latestHistory);
            
            // 書籍感想有無の判定
            boolean hasReviewHistory = false;
            if(dtoBookHistory.stream().anyMatch(h -> h.getReview() == null)) {
            	hasReviewHistory = true;
            }
            model.addAttribute("hasReviewHistory", hasReviewHistory);

            if (bookId == null) {
                redirectAttributes.addFlashAttribute("error", "書籍IDが取得できませんでした");
                return "redirect:/page/top";
            }

            

            
            return "page/ReturnCompleted";
            
        } catch (Exception e) {
            return error(redirectAttributes);
        }

    }
    
    /**
     * パスワード変更ページに遷移
     */
    @GetMapping("/page/changePassword")
    public String changePassword(Model model) {
    	model.addAttribute("changePasswordDto", new DtoChangePassword());
    	return "page/changePassword";
    }
    
    /**
	 * パスワード変更処理
	 */
    @PostMapping("/action/changePassword")
    public String changePasswordAction(
    		@Valid @ModelAttribute("changePasswordDto") DtoChangePassword changePasswordDto,
    		BindingResult bindingResult,
    		RedirectAttributes redirectAttributes,
    		Model model) {
    	
    	if (bindingResult.hasErrors()) {
			model.addAttribute("errorMessage", "パスワード変更に失敗しました。\n入力内容を確認してください。");
			return "page/changePassword";
		}
    	
    	try {
			// パスワード変更処理
			tUserService.changePassword(changePasswordDto);
			// 完了画面に表示するメールアドレスを取得
			redirectAttributes.addFlashAttribute("mailAddress", changePasswordDto.getMailAddress());
			return "redirect:/page/changePasswordCompleted";
		
    	} catch (IllegalArgumentException e) {
    		model.addAttribute("errorMessage", e.getMessage());
    		return "page/changePassword";
    		
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			logger.error("パスワード変更処理でエラーが発生しました", e);
			return "page/changePassword";
    	}
    }
    
    /**
	 * パスワード変更完了ページに遷移
	 */
    @GetMapping("/page/changePasswordCompleted")
    public String changePasswordCompleted(
    		RedirectAttributes redirectAttributes) {
    	
    	try {
    		logger.info("changePasswordCompletedにアクセスされました");
    		
    		return "page/changePasswordCompleted";
    	} catch (Exception e) {
			return error(redirectAttributes);
    	}
	}

    /**
     * エラー処理
     * セッション切れなどの際にloginページにリダイレクト
     */
    protected String error(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "セッションが切れました。再度ログインしてください。");
        return "redirect:/page/login";
    }

    /**
     * 管理者権限エラー処理
     * 管理者権限が必要な機能にアクセスした際にloginページにリダイレクト
     */
    protected String adminPermissionError(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "管理者権限が必要です。");
        return "redirect:/page/login";
    }

    /**
     * JWT認証とユーザーID取得の共通処理
     */
    protected Integer validateTokenAndGetUserId(HttpSession session) throws Exception {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            throw new Exception("Token not found in session");
        }
        return jwtUtil.extractUserId(token);
    }

    /**
     * JWT認証と管理者権限確認の共通処理
     */
    protected boolean validateTokenAndCheckAdmin(HttpSession session) throws Exception {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            throw new Exception("Token not found in session");
        }
        jwtUtil.extractUserId(token); // トークンの有効性確認

        Integer userId = jwtUtil.extractUserId(token);
        session.setAttribute("currentUserId", userId);

        return jwtUtil.extractIsAdmin(token);
    }

}