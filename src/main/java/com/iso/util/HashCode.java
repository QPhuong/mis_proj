package com.iso.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class HashCode {
	
	public static String getHashPassword(String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(password);
		return hashedPassword;
	}
	
}
