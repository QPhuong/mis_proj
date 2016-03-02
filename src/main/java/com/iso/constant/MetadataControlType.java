package com.iso.constant;

public enum MetadataControlType {
	INPUT("input"), DATE("date"), DATETIME("datetime"), BOOLEAN("boolean"), TEXTAREA("textarea"), NUMBER("number");
	
	private final String text;

	private MetadataControlType(String text) {
		this.text = text;
	}
	
	public String toString(){
	       return text;
    }
	
	public String toDisplayString() {
		switch(this) {
			case INPUT: return "Input Text";
			case DATE: return "Date";
			case DATETIME: return "Date Time";
			case BOOLEAN: return "Checkbox";
			case TEXTAREA: return "Text Area";
			case NUMBER: return "Number";
			default : return "Input Text";
		}
		
	}
}
