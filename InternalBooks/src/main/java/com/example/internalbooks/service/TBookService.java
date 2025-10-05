package com.example.internalbooks.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internalbooks.dto.DtoBookInfo;
import com.example.internalbooks.entity.TBookEntity;
import com.example.internalbooks.repository.TBookRepository;

@Service
@Transactional
/**
 * TBookテーブルに対してどんな操作をしていくかをTBookリポジトリを介して制御していくサービス
 */
public class TBookService {

	//DI用フィールド
    private final TBookRepository tBookRepository;

	//コンストラクタインジェクション
    public TBookService(TBookRepository tBookRepository) {
        this.tBookRepository = tBookRepository;
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

	/**
	 * TBookEntityからDtoBookInfoに変換する共通メソッド
	 */
	private DtoBookInfo convertEntityToDto(TBookEntity book) {
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
		
		// 貸出状況を設定（borrower_idに基づいて動的に判定）
		dto.setStatus(determineLendingStatus(book.getBorrowerId()));
		
		// 書籍IDに基づいて画像URLを設定
		dto.setImageUrlFromBookId();
		
		return dto;
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
			checkedOutBooks.add(convertEntityToDto(book));
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
			
			// EntityからDTOに変換
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
			
			// 返却 感想・コメント
			dto.setMemo(book.getMemo());
			
			return dto;
			
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
	 * 書籍IDを取得するメソッド
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

}
