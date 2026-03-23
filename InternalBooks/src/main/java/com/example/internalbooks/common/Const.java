package com.example.internalbooks.common;

/**
 * アプリ内で共通で使用される変数の定義用クラス
 */
public class Const {
	public final static String ID = "id";
	public final static String USER_ID = "user_id";
	public final static String NAME = "name";
	public final static String MAILADDRESS = "mail_address";
	public final static String PASSWORD = "password";
	public final static String ROLE = "role";
	public final static String DEPARTMENT_ID = "department_id";
	public final static String DELETE_FLG = "delete_flg";
	public final static String BOOK_ID = "book_id";
	public final static String TITLE = "title";
	public final static String CATEGORY = "category";
	public final static String CATEGORIES = "categories";
	public final static String BORROWER_ID = "borrower_id";
	public final static String PROVIDER_ID = "provider_id";

	public final static String PROVIDER_COMMENT = "provider_comment";
	public final static String MEMO = "memo";

	public final static String DETAIL = "detail";

	public final static String LENDING_DATE = "lending_date";
	public final static String RETURN_DATE = "return_date";
	public final static String SCHEDULED_RETURN_DATE = "scheduled_return_date";
	public final static String REVIEW = "review";

	public final static Integer PLUS_KIZONBOOKID = 1;
	public final static Integer PLUS_NEWBOOKID = 10001;

	/** 書籍一覧の1ページあたり表示件数 */
	public static final int BOOKS_PER_PAGE = 6;

	/** カテゴリー一覧の1ページあたり表示件数（2列×5行） */
	public static final int CATEGORIES_PER_PAGE = 10;

	/**
	 * ユーザー削除フラグ
	 * 0: 有効ユーザー
	 * 1: 削除ユーザー
	 */
	public static final int DELETE_FLAG_OFF = 0;
	public static final int DELETE_FLAG_ON = 1;

}
