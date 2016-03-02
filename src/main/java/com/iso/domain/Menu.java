package com.iso.domain;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.iso.domain.base.BaseDomain;

@Document
public class Menu extends BaseDomain{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Field
	private String code;
	@Field
	private String name;
	@Transient
	private boolean isGroup;
	
	public Menu(){}
	
	public Menu(String code, String name){
		this.code = code;
		this.name = name;
		this.isGroup = false;
	}
	public Menu(String code, String name, boolean isGroup){
		this.code = code;
		this.name = name;
		this.isGroup = isGroup;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isGroup() {
		return isGroup;
	}
	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}
	
}
