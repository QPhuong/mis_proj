package com.iso.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;


public class DomainVersionGenerator {

	public static String generateNewVerion(String latestVersion, boolean isMajorVersion) {
		
		if(StringUtils.isEmpty(latestVersion)) {
			return "1.0";
		}
		
		String newVersion = "";
		List<String> versions = new ArrayList<String>(Arrays.asList(latestVersion.split("\\.")));

		if(versions.size() > 0) {
			if (isMajorVersion) {
				String mainVersion = String.valueOf(Integer.parseInt(versions.get(0)) + 1);
				versions.set(0, String.valueOf(Integer.parseInt(versions.get(0)) + 1));
				int size = versions.size();
				versions.clear();
				versions.add(mainVersion);
				for (int i = 0; i < size - 1; i++) {
					versions.add("0");
				}
			} else {
				if (versions.size() > 1) {
					versions.set(versions.size() - 1, String.valueOf(Integer.parseInt(versions.get(versions.size() - 1))  + 1));
				} else {
					versions.add("1");
				}
			}
			for (int i = 0; i < versions.size() -1; i++) {
				newVersion += versions.get(i) + ".";
			}
			newVersion += versions.get(versions.size() - 1);
		}
		
		return newVersion;
	}
}
