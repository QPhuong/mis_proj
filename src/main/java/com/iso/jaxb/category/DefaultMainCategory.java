package com.iso.jaxb.category;

import java.io.File;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="category")
public class DefaultMainCategory {
	
	private String code;
	
	private String name;
	
	private String description;
	
	private List<DefaultMainCategory> subcategories;
	
	private List<File> fileTemplates;
	
	public String getName() {
		return name;
	}
	
	public String getCode() {
		return code;
	}
	@XmlElement
	public void setCode(String code) {
		this.code = code;
	}
	
	@XmlElement
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	@XmlElement
	public void setDescription(String description) {
		this.description = description;
	}

	public List<DefaultMainCategory> getSubcategories() {
		return subcategories;
	}

	@XmlElementWrapper(name = "subcategories")
	@XmlElement(name = "category")
	public void setSubcategories(List<DefaultMainCategory> subcategories) {
		this.subcategories = subcategories;
	}
	
	public List<File> getFileTemplates() {
		return fileTemplates;
	}
	public void setFileTemplates(List<File> fileTemplates) {
		this.fileTemplates = fileTemplates;
	}
	
}
