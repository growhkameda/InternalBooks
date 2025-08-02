package com.example.internalbooks.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internalbooks.dto.DtoCheckedOutBook;
import com.example.internalbooks.entity.TBookEntity;
import com.example.internalbooks.repository.TBookRepository;

@Service
@Transactional
/**
 * TBookテーブルに対してどんな操作をしていくかをTBookリポジトリを介して制御していくサービス
 */
public class TBookService {

	@Autowired
	private TBookRepository tBookRepository;

	public List<String> getAllCategories() {
		List<String> categoryList = new ArrayList<>();
		try {

			// 全本情報を取得
			List<TBookEntity> bookList = tBookRepository.findAll();

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

	// 指定されたユーザーIDの貸出中書籍を取得する
	public List<DtoCheckedOutBook> getCheckedOutBooksByUserId(Integer userId) {
		List<DtoCheckedOutBook> checkedOutBooks = new ArrayList<>();
		
		try {
			System.out.println("★★★ TBookService: ユーザーID " + userId + " の貸出中書籍を検索開始 ★★★");
			
			// 借りているユーザーIDで書籍を検索
			List<TBookEntity> borrowedBooks = tBookRepository.findByBorrowerId(userId);
			
			System.out.println("★★★ TBookService: データベースから取得した書籍数 = " + borrowedBooks.size() + " ★★★");
			
			// EntityからDTOに変換
			for (TBookEntity book : borrowedBooks) {
				System.out.println("★★★ TBookService: 書籍変換中 - ID:" + book.getBookId() + 
								  ", タイトル:" + book.getTitle() + 
								  ", borrower_id:" + book.getBorrowerId() + " ★★★");
				
				DtoCheckedOutBook dto = new DtoCheckedOutBook();
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
				
				// 書籍IDに基づいて画像URLを設定
				dto.setImageUrlFromBookId();
				
				checkedOutBooks.add(dto);
			}
			
			System.out.println("★★★ TBookService: 変換完了 - 最終的なDTO数 = " + checkedOutBooks.size() + " ★★★");
			
		} catch (Exception e) {
			System.out.println("★★★ TBookService: エラー発生 - " + e.getMessage() + " ★★★");
			e.printStackTrace();
			throw e;
		}
		
		return checkedOutBooks;
	}

}
