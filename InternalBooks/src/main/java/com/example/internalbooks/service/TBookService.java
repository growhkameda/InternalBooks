package com.example.internalbooks.service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.internalbooks.common.Const;
import com.example.internalbooks.dto.DtoBookInfo;
import com.example.internalbooks.entity.TBookEntity;
import com.example.internalbooks.entity.TLendingHistoryEntity;
import com.example.internalbooks.entity.TUserEntity;
import com.example.internalbooks.repository.TBookRepository;
import com.example.internalbooks.repository.TLendingHistoryRepository;
import com.example.internalbooks.repository.TUserRepository;

@Service
@Transactional
/**
 * TBookテーブルに対してどんな操作をしていくかをTBookリポジトリを介して制御していくサービス
 */
public class TBookService {

	private static final Logger logger = LoggerFactory.getLogger(TBookService.class);

	// DI用フィールド
	private final TBookRepository tBookRepository;
	private final TLendingHistoryRepository lendingHistoryRepository;
	private final TUserRepository tUserRepository;
	private final TUserService tUserService;
	private final ImageStorageService imageStorageService;

	// コンストラクタインジェクション
	public TBookService(TBookRepository tBookRepository, TLendingHistoryRepository lendingHistoryRepository,
			TUserService tUserService, ImageStorageService imageStorageService, TUserRepository tUserRepository) {
		this.tBookRepository = tBookRepository;
		this.lendingHistoryRepository = lendingHistoryRepository;
		this.tUserService = tUserService;
		this.imageStorageService = imageStorageService;
		this.tUserRepository = tUserRepository;
	}

	public List<String> getAllCategories() {
		List<String> categoryList = new ArrayList<>();
		try {
			// 全本情報をbook_idの昇順で取得
			List<TBookEntity> bookList = tBookRepository.findAll(Sort.by(Sort.Direction.ASC, "bookId"));

			// カテゴリー情報を登録されている本情報から取得する
			for (TBookEntity book : bookList) {
				// カンマ区切りのカテゴリを分割し、重複しないように値を格納
			String[] categories = book.getCategories().split(",");
			for (String category : categories) {
				String trimmed = category.strip();
				if (!categoryList.contains(trimmed)) {
					categoryList.add(trimmed);
				}
			}
			}
		} catch (Exception e) {
			throw e;
		}
		return categoryList;
	}

	/** 指定ページのカテゴリー一覧を返す */
	public List<String> getPagedCategories(int page, int pageSize) {
		List<String> all = getAllCategories();
		int fromIndex = page * pageSize;
		if (fromIndex >= all.size()) return new ArrayList<>();
		int toIndex = Math.min(fromIndex + pageSize, all.size());
		return all.subList(fromIndex, toIndex);
	}

	/** カテゴリーの総ページ数を返す */
	public int getCategoryTotalPages(int pageSize) {
		return (int) Math.ceil((double) getAllCategories().size() / pageSize);
	}

	public List<Integer> getCategoriesdetail(String category) {
		List<Integer> bookid_list = new ArrayList<>();
		try {

			// 全本情報を取得
			List<TBookEntity> bookList = tBookRepository.findAll();

			// カテゴリー情報を登録されている本情報から取得する
			for (TBookEntity book : bookList) {
				String book_categories = book.getCategories();
				// 複数のカテゴリーをカンマ区切りにする
				String[] categoriesArray = book_categories.split(",");
				for (String list_category : categoriesArray) {
					// 引数のカテゴリーの値が含まれている本情報のみbookid_listに追加する
					if (list_category.strip().equals(category.strip())) {
						bookid_list.add(book.getBookId());
						break;
					}
				}
			}

		} catch (Exception e) {
			throw e;
		}
		return bookid_list;
	}

	/**
	 * カテゴリーに属する書籍の詳細情報を取得
	 * 書籍削除画面で貸出状態を判定するために使用
	 */
	public List<DtoBookInfo> getBooksByCategoryWithDetails(String category) {
		List<DtoBookInfo> bookList = new ArrayList<>();
		try {
			// 全書籍を取得
			List<TBookEntity> allBooks = tBookRepository.findAll();

			// カテゴリーに一致する書籍を抽出してDTOに変換
			for (TBookEntity book : allBooks) {
				String bookCategories = book.getCategories();
				// 複数のカテゴリーをカンマ区切りで分割
				String[] categoriesArray = bookCategories.split(",");
				for (String bookCategory : categoriesArray) {
					// 引数のカテゴリーに一致する書籍のみ追加
					if (bookCategory.strip().equals(category.strip())) {
						// convertEntityToDtoで書籍情報をDTOに変換（status含む）
						DtoBookInfo dto = convertEntityToDto(book, false);
						bookList.add(dto);
						break; // 同じ書籍を複数回追加しないよう抜ける
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return bookList;
	}

	/**
	 * 11/02 木俣 (DTO内の責務が混ざっていたので分離)
	 * TBookEntityからDtoBookInfoに変換するメソッド
	 * 変換を責務とし、DtoBookInfoに受け渡す
	 */
	private DtoBookInfo convertEntityToDto(TBookEntity book, boolean includeReturnDate) {
		DtoBookInfo dto = new DtoBookInfo();
		dto.setBookId(book.getBookId());

		// タイトルが空の場合はデフォルト値を設定
		String title = book.getTitle();
		if (title == null || title.trim().isEmpty()) {
			title = "書籍ID: " + book.getBookId();
		}
		dto.setTitle(title);

		// カテゴリーが空の場合はデフォルト値を設定
		String categories = book.getCategories();
		if (categories == null || categories.trim().isEmpty() || categories.equals(",")) {
			categories = "未分類";
		}
		dto.setCategory(categories);

		// カテゴリー（分割リストを保持）
		if (!categories.equals("未分類")) {
			dto.setCategories(Arrays.asList(categories.split(",")));
		} else {
			dto.setCategories(new ArrayList<>());
		}

		// 貸出状況を設定（borrower_idに基づいて動的に判定）
		dto.setStatus(determineLendingStatus(book.getBorrowerId(), book.getBookId()));

		// 書籍IDに基づいて画像URLを設定
		dto.setImageUrlFromBookId();

		// 書籍提供者コメントを設定
		dto.setProviderComment(book.getProviderComment());

		// 書籍提供者名を取得して設定（providerが取得できない/名前が空の場合は共通名にフォールバック）
		String providerName = null;
		if (book.getProviderId() != null) {
			TUserEntity provider = tUserService.getUserById(book.getProviderId());
			if (provider != null) {
				providerName = provider.getName();
			}
		}
		if (providerName == null || providerName.isBlank()) {
			// providerIdがnull／提供者が存在しない／名前が空の場合「グロウ　太郎」と表示
			providerName = Const.COMMON_PROVIDER_NAME;
		}
		dto.setProviderName(providerName);

		// 返却 感想・コメント
		dto.setMemo(book.getMemo());

		// 返却予定日を取得して設定（オプション：貸出中書籍リストの場合のみ）
		if (includeReturnDate && book.getBorrowerId() != null) {
			setScheduledReturnDate(dto, book.getBookId());
		}

		return dto;
	}

	/**
	 * 書籍IDから返却予定日を取得してDTOに設定する
	 */
	private void setScheduledReturnDate(DtoBookInfo dto, Integer bookId) {
		List<TLendingHistoryEntity> histories = lendingHistoryRepository.findByBookId(bookId);
		if (!histories.isEmpty()) {
			TLendingHistoryEntity latestHistory = histories.get(0);
			if (latestHistory.getScheduledReturnDate() != null) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日(E)");
				dto.setScheduledReturnDate(latestHistory.getScheduledReturnDate().format(formatter));
			} else {
				dto.setScheduledReturnDate("-");
			}
		} else {
			dto.setScheduledReturnDate("-");
		}
	}

	/**
	 * 指定されたユーザーIDの貸出中書籍を取得する
	 */
	public List<DtoBookInfo> getCheckedOutBooksByUserId(Integer userId) {
		List<DtoBookInfo> checkedOutBooks = new ArrayList<>();

		// 借りているユーザーIDで書籍を検索
		List<TBookEntity> borrowedBooks = tBookRepository.findByBorrowerId(userId);

		// EntityからDTOに変換（共通メソッドを使用）
		for (TBookEntity book : borrowedBooks) {
			checkedOutBooks.add(convertEntityToDto(book, true));
		}

		return checkedOutBooks;
	}

	/**
	 * 指定されたbookIdの書籍情報を取得する
	 */
	public DtoBookInfo getBookById(Integer bookId) {
		try {
			// 書籍IDで書籍を検索
			TBookEntity book = tBookRepository.findById(bookId).orElse(null);

			if (book == null) {
				return null;
			}

			// 共通メソッドでEntityからDTOに変換（返却予定日は不要）
			return convertEntityToDto(book, false);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 書籍検索リクエストを処理する
	 */
	public DtoBookInfo processBookSearchRequest(Integer bookIdParam, String qrData) {
		try {
			// 書籍IDを解決する（パラメータまたはQRデータから）
			Integer bookId = resolveBookId(bookIdParam, qrData);

			// 書籍IDが取得できない場合はnullを返す
			if (bookId == null) {
				return null;
			}

			// 書籍情報を取得
			return getBookById(bookId);

		} catch (Exception e) {
			// エラーが発生した場合はnullを返してエラー表示させる
			logger.error("書籍検索処理でエラーが発生しました: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * QRコードから書籍IDを取得するメソッド
	 * テストのし易さを考えて指定Idを優先して取得する
	 */
	private Integer resolveBookId(Integer bookIdParam, String qrData) {
		// 直接指定されたbookIdが優先
		if (bookIdParam != null) {
			return bookIdParam;
		}

		// QRデータから解析
		if (qrData != null && !qrData.isEmpty()) {
			try {
				return Integer.parseInt(qrData);
			} catch (NumberFormatException e) {
				// QRデータが無効な場合は例外をスロー
				throw new IllegalArgumentException("無効なQRコードデータ: " + qrData);
			}
		}

		// どちらも指定されていない場合はnull
		return null;
	}

	private String determineLendingStatus(Integer borrowerId, Integer bookId) {
		if (borrowerId != null) {
			return "貸出中";
		}
		// borrowerIdがnullでも、履歴テーブル上で未返却のレコードがあれば貸出中とみなす（データ不整合への対抗策）
		List<TLendingHistoryEntity> histories = lendingHistoryRepository.findByBookId(bookId);
		if (!histories.isEmpty()) {
			TLendingHistoryEntity latest = histories.get(0);
			if (latest.getReturnDate() == null) {
				return "貸出中";
			}
		}
		return "貸出可能";
	}
	
	
	/**
	 * 指定された書籍IDが指定されたユーザーIDによって借りられているかを確認するメソッド
	 */
	public boolean isBookBorrowedByUser(Integer bookId, Integer userId) {
		TBookEntity bookEntity = tBookRepository.findById(bookId).orElse(null);
		if (bookEntity == null) {
			return false;
		}
		return userId.equals(bookEntity.getBorrowerId());
	}

	/**
	 * 書籍画像を処理するメソッド
	 */
	public void tbookconfirm(DtoBookInfo dtbook) throws IOException {

		MultipartFile file = dtbook.getImageFile();

		// 書籍画像の処理後dtoにセット
		if (file != null && !file.isEmpty()) {
			String imageUrl = imageStorageService.savetbook(file);
			dtbook.setImageUrl(imageUrl);
		}
	}

	/**
	 * 書籍登録するメソッド
	 */
	public DtoBookInfo bookEditing(DtoBookInfo dtbook) {
		Integer providerUserId;
		try {
			providerUserId = Integer.valueOf(dtbook.getProviderId().trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("書籍提供者の指定が不正です", e);
		}

		TUserEntity user = tUserRepository.findById(providerUserId)
				.orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

		if (!Integer.valueOf(Const.DELETE_FLAG_OFF).equals(user.getDeleteFlg())) {
			throw new IllegalArgumentException("選択されたユーザーは利用できません");
		}

		dtbook.setId(user.getUserId());

		try {
			// t_bookへ登録
			TBookEntity tbook = new TBookEntity();
			tbook.setTitle(dtbook.getTitle());
			tbook.setCategories(dtbook.getCategory());
			tbook.setProviderId(dtbook.getId());
			tbook.setProviderComment(dtbook.getProviderComment());
			// 既存のカテゴリ名の最大値を取得
			Integer maxId = tBookRepository.findMaxIdByName(dtbook.getCategory());
			// 取得した最大値+1
			int nextId;
			if (maxId != null) {
				// 既存のカテゴリ名に+1
				nextId = maxId + Const.PLUS_KIZONBOOKID;
			} else {
				// 既存のカテゴリ名がない場合
			Integer maxIdAll = tBookRepository.findMaxBookId();
			// データがある場合はその最大値に10001をたす。DBが空の場合は0として扱う
			nextId = (maxIdAll != null ? maxIdAll : 0) + Const.PLUS_NEWBOOKID;
			}
		// 取得したIDをセット
		tbook.setBookId(nextId);
		// DBへセットした値を保存
		TBookEntity saved = tBookRepository.save(tbook);
		// ユーザー名をTbookEntityの提供者名にセット
		saved.setProviderName(user.getName());

		// 確認ステップで元ファイル名として保存した画像を {bookId}.png にリネーム
		// リネーム失敗はDB登録に影響させない（ログのみ記録して継続）
		String currentFileName = dtbook.getImageUrl();
		if (currentFileName != null && !currentFileName.isBlank()) {
			try {
				imageStorageService.renameToBookId(currentFileName, saved.getBookId());
			} catch (Exception e) {
				logger.warn("画像のリネームに失敗しました（書籍登録は継続します）: currentFileName={}, bookId={}, error={}",
						currentFileName, saved.getBookId(), e.getMessage(), e);
			}
		}

		return toRegistrationCompleteDto(saved);

	} catch (DataIntegrityViolationException e) {
		throw new IllegalStateException("登録に失敗しました", e);
	}

	}

	/** 書籍登録完了画面用にEntityをDTOへ変換する */
	private DtoBookInfo toRegistrationCompleteDto(TBookEntity saved) {
		DtoBookInfo dto = new DtoBookInfo();
		dto.setTitle(saved.getTitle());
		dto.setProviderId(saved.getProviderName());
		dto.setCategory(saved.getCategories());
		dto.setProviderComment(saved.getProviderComment());
		dto.setBookId(saved.getBookId());
		dto.setImageUrlFromBookId();
		return dto;
	}

	/** 指定カテゴリーの指定ページの書籍リストを返す */
	public List<DtoBookInfo> getPagedBooksByCategory(String category, int page, int pageSize) {
		List<DtoBookInfo> all = getBooksByCategoryWithDetails(category);
		int fromIndex = page * pageSize;
		if (fromIndex >= all.size()) return new ArrayList<>();
		int toIndex = Math.min(fromIndex + pageSize, all.size());
		return all.subList(fromIndex, toIndex);
	}

	/** 指定カテゴリーの総ページ数を返す */
	public int getBooksByCategoryTotalPages(String category, int pageSize) {
		return (int) Math.ceil((double) getBooksByCategoryWithDetails(category).size() / pageSize);
	}

	/**
	 * 11/03 木俣
	 * 指定されたbook_idの書籍を削除する
	 * DBでカスケード処理していないためBEでカスケード処理を行う
	 * 画像ファイルも同時に削除する
	 */
	public boolean deleteBookById(Integer bookId) {

		try {
			Optional<TBookEntity> book = tBookRepository.findById(bookId);

			// 書籍の存在を確認
			if (book.isEmpty()) {
				return false;
			}

			// 書籍が貸出中かチェック
			TBookEntity bookEntity = book.get();
			if (bookEntity.getBorrowerId() != null) {
				throw new IllegalStateException("書籍は貸出中のため削除できません");
			}

			// 画像ファイルを削除（DB削除の前に実行）
			// 画像削除に失敗してもDB削除は継続する
			try {
				imageStorageService.deleteImage(bookId);
			} catch (Exception e) {
				// 画像削除エラーはログに記録するが、処理は継続
				logger.error("画像削除処理でエラーが発生しました（書籍削除は継続します）: bookId={}, error={}", bookId, e.getMessage());
			}

			// 貸出履歴をカスケード削除
			lendingHistoryRepository.deleteByBookId(bookId);
			// 書籍を削除
			tBookRepository.deleteById(bookId);

			return true;

		} catch (IllegalStateException e) {
			// 貸出中の場合はそのまま再スロー
			throw e;

		} catch (Exception e) {
			throw new RuntimeException("書籍の削除に失敗しました", e);
		}

	}

}
