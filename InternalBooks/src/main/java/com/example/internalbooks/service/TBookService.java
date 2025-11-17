package com.example.internalbooks.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	//DI用フィールド
    private final TBookRepository tBookRepository;
    private final TLendingHistoryRepository lendingHistoryRepository;
    private final TUserRepository tUserRepository;
    private final TUserService tUserService;
    private final ImageStorageService imageStorageService;

	//コンストラクタインジェクション
    public TBookService(TBookRepository tBookRepository, TLendingHistoryRepository lendingHistoryRepository, TUserService tUserService, ImageStorageService imageStorageService,TUserRepository tUserRepository) {
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
					if (!categoryList.contains(category)) {
						categoryList.add(category);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return categoryList;
	}

	
	public List<Integer> getCategoriesdetail(String category){
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
				    if (list_category.trim().equals(category)) {
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
					if (bookCategory.trim().equals(category)) {
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

	/** 11/02 木俣 (DTO内の責務が混ざっていたので分離)
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
		dto.setStatus(determineLendingStatus(book.getBorrowerId()));
		
		// 書籍IDに基づいて画像URLを設定
		dto.setImageUrlFromBookId();
		
		// 書籍提供者コメントを設定
		dto.setProviderComment(book.getProviderComment());
		
		// 書籍提供者名を取得して設定
		if (book.getProviderId() != null) {
			try {
				TUserEntity provider = tUserService.getUserById(book.getProviderId());
				if (provider != null) {
					dto.setProviderName(provider.getName());
				}
			} catch (Exception e) {
				// ユーザーが見つからない場合はnullのまま
				dto.setProviderName(null);
			}
		}
		
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
			System.err.println("書籍検索処理でエラーが発生しました: " + e.getMessage());
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
	
	/**
	 * borrower_idに基づいて貸出状況を判定する共通メソッド
	 * 貸出状況を取得する際にはこのメソッドを共通で使うようにしてください！
	 */
	private String determineLendingStatus(Integer borrowerId) {
		return borrowerId != null ? "貸出中" : "貸出可能";
	}
	
	public TBookEntity bookEditing(DtoBookInfo dtbook) {
        //書籍提供者とユーザーIDの紐付け
		TUserEntity user = tUserRepository.findByName(dtbook.getProviderId())
                              .orElseThrow(() -> new RuntimeException("名前が見つかりません"));
        //紐付けたユーザーIDをDTOにセット
		dtbook.setId(user.getUserId());
		
        //t_bookへ登録
		TBookEntity tbook = new TBookEntity();
		tbook.setTitle(dtbook.getTitle());
		tbook.setCategories(dtbook.getCategory());
		tbook.setProviderId(dtbook.getId());
		tbook.setProviderComment(dtbook.getProviderComment());
		
        //その他の時は書籍ID9999へセット	
		switch(dtbook.getCategory()) {
		  case "その他":
			  tbook.setBookId(9999);
			  break;
	    //上記以外はカテゴリ名よりIDを取得し最大値に＋１
	    default:
		      Integer maxId = tBookRepository.findMaxIdByName(dtbook.getCategory());
              if(maxId != null) {
			     tbook.setBookId(maxId + 1);
		}
	}	
		TBookEntity saved= tBookRepository.save(tbook);
		//ユーザー名をTbookEntityの提供者名にセット
		saved.setProviderName(user.getName());
		
		
		return saved;
	}

	/** 11/03 木俣
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
				System.err.println("画像削除処理でエラーが発生しました（書籍削除は継続します）: bookId=" + bookId + ", error=" + e.getMessage());
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
