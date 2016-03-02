package com.iso.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import com.iso.domain.Category;
import com.iso.domain.IsoDocument;

public final class IsoFileSupportUtils {

	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    //String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    String pre = "KMGTPE".charAt(exp-1) + (si ? "i" : "");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	public static String humanReadableByteCount(long bytes) {
	    return humanReadableByteCount(bytes, false);
	}
	
	public static String getFileExtension(String fileName) {
		return FilenameUtils.getExtension(fileName);
	}

	public static String getFileTitle(String fileName) {
		return FilenameUtils.getName(fileName);
	}
	
	private static String generateLocation(Category category) {
		String location = (category != null ? category.getName() : "/");
		if (category != null && category.getParent() != null) {
			location = generateLocation(category.getParent()) + File.separator + location;
		}
		return location;
	}
	
	public static String getLocation(Category category) {
		return generateLocation(category.getParent());
	}
	
	public static String getLocation(IsoDocument document) {
		return generateLocation(document.getCategory());
	}
}
