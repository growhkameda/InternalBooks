package com.example.internalbooks.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
