package com.iso.constant;

public enum LogTypes {
	ADMIN_USER_MANAGEMENT_LOG("Administration - User Management"), 
	ADMIN_USER_ROLE_MANAGEMENT_LOG("Administration - User Role Management"),
	ADMIN_ORGANIZATION_MANAGEMENT_LOG("Administration - Organization Management"),
	ADMIN_ORGANIZATION_UNIT_MANAGEMENT_LOG("Administration - Organization Structure Management"),
	ADMIN_MENU_SECURITY_MANAGEMENT_LOG("Administration - Menu Security Management"),
	ADMIN_MAIN_CATEGORIES_MANAGEMENT_LOG("Administration - Main Categories Management"),
	DOCUMENT("Document"),
	CATEGORY("Category");

	private final String text;

	private LogTypes(String text) {
		this.text = text;
	}
	
	public String toString(){
	       return text;
    }
}
