package com.iso.util;

import java.text.MessageFormat;
import java.util.List;


public class MessageUtils {
	
	/**
	 * MessageFormatPattern:
	         String
	         MessageFormatPattern FormatElement String

	 FormatElement:
	         { ArgumentIndex }
	         { ArgumentIndex , FormatType }
	         { ArgumentIndex , FormatType , FormatStyle }

	 FormatType: one of 
	         number date time choice

	 FormatStyle:
	         short
	         medium
	         long
	         full
	         integer
	         currency
	         percent
	         SubformatPattern
	 */
	public static String generate(String message, List<String> param) {
		return MessageFormat.format(message, param.toArray());
	}
	
	public static String generate(String message, String... param) {
		return MessageFormat.format(message, param);
	}
	
}
