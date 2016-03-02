package com.iso.constant;

public enum SortType {
	ASC("Ascending"), DESC("Descending");
	
	private final String text;

	private SortType(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
}
