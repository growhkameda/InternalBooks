package com.example.internalbooks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
/**
 * アプリのコントローラークラス
 */
public class InternalBooksController {
	
	@GetMapping("hello")
	/**
	 * Hello Worldを表示(index.xtml)を表示するメソッド
	 */
	public String hello() {
		return "index";
	}
}
