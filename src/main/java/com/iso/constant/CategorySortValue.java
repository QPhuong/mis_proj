package com.iso.constant;

public enum CategorySortValue {
	NAME("name", "Name"), DATE_CREATED("createdOn", "Date Created"), DATE_UPDATED("updatedOn", "Date Modified");
	
	private final String fieldName;
	private final String displayedName;
	
	private  CategorySortValue(String fieldName, String displayedName) {
		this.fieldName = fieldName;
		this.displayedName = displayedName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getDisplayedName() {
		return displayedName;
	}
	
}
