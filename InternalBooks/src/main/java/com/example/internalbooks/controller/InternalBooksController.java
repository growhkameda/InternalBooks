package com.example.internalbooks.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InternalBooksController {
	
	private static final Logger logger = LoggerFactory.getLogger(InternalBooksController.class);

    @GetMapping("/")
    public String index() {
        return "index";
    }
    @GetMapping("/test/kimata")
    public String kimata() {
    	logger.info("★★★★★★★★★★★kimata() にアクセスされました");
        return "test/kimata";
    }
    @GetMapping("/test/matunaga")
    public String matunaga() {
    	logger.info("★★★★★★★★★★★matunaga() にアクセスされました");
        return "test/matunaga";
    }
    @GetMapping("/test/mutou")
    public String mutou() {
    	logger.info("★★★★★★★★★★★mutou() にアクセスされました");
        return "test/mutou";
    }
    @GetMapping("/test/sano")
    public String sano() {
    	logger.info("★★★★★★★★★★★sano() にアクセスされました");
        return "test/sano";
    }
    @GetMapping("/test/kameda")
    public String kameda() {
    	logger.info("★★★★★★★★★★★kameda() にアクセスされました");
        return "test/kameda";
    }
    @GetMapping("/test/amemiya")
    public String amemiya() {
    	logger.info("★★★★★★★★★★★amemiya() にアクセスされました");
        return "test/amemiya";
    }
    @GetMapping("/test/qr_test")
    public String qrTest() {
    	logger.info("★★★★★★★★★★★qrTest() にアクセスされました");
        return "test/qr_test";
    }
}