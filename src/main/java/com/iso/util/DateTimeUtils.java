package com.iso.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtils {

	public static String formatDateTime(Date date) {
		if(date != null) {
			return new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(date);
		}else {
			return "";
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Calendar default timezone: " + Calendar.getInstance().getTimeZone());
		System.out.println("Calendar instance: " + Calendar.getInstance().getTime());
		System.out.println("Calendar instance UTC: " + Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());
		
	}
}
