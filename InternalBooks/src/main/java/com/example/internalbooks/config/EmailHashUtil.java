package com.example.internalbooks.config;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component

public class EmailHashUtil {
	
	 public String hash(String email) {
	    	if (email == null) return null;
	    	return DigestUtils.sha256Hex(email.trim().toLowerCase());
	    }

}
