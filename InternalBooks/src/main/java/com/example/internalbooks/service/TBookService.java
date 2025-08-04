package com.example.internalbooks.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
	 * 指定されたユーザーIDの貸出中書籍を取得する
	 */
	public List<DtoCheckedOutBook> getCheckedOutBooksByUserId(Integer userId) {
		List<DtoCheckedOutBook> checkedOutBooks = new ArrayList<>();
		
		// 借りているユーザーIDで書籍を検索
		List<TBookEntity> borrowedBooks = tBookRepository.findByBorrowerId(userId);
		
		// EntityからDTOに変換
		for (TBookEntity book : borrowedBooks) {
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
		
		return checkedOutBooks;
	}

}
