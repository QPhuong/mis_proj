package com.iso.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.iso.domain.base.BaseDomain;
import com.iso.jaxb.category.DefaultMainCategory;

@Document
public class MainCategory extends BaseDomain {

	private static final long serialVersionUID = 1L;

	@Id
	private ObjectId id;
	@Field
	private String code;
	@Field
	private String name;
	@Field
	private String description;
	@DBRef
	private MainCategory parent;
	@Field
	private boolean root;
	@DBRef
	private MainCategoryType type;
	@DBRef
	private List<IsoDocument> lstTemplates;
	@Transient
	private List<File> lstTemplateDefault;
	
	public MainCategory() {
		lstTemplates= new ArrayList<IsoDocument>();
	}
	
	public MainCategory(DefaultMainCategory defaultCategory, MainCategory parent, MainCategoryType mainCategoryType) {
		this.name = defaultCategory.getName();
		this.code = defaultCategory.getCode();
		this.description = defaultCategory.getDescription();
		this.parent = parent;
		this.root = (parent == null);
		this.type = mainCategoryType;
		
		this.lstTemplates = new ArrayList<IsoDocument>();
		
		this.lstTemplateDefault = new ArrayList<File>();
		this.lstTemplateDefault.addAll(defaultCategory.getFileTemplates());
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public MainCategory getParent() {
		return parent;
	}
	public void setParent(MainCategory parent) {
		this.parent = parent;
	}
	public boolean isRoot() {
		return root;
	}
	public void setRoot(boolean isRoot) {
		this.root = isRoot;
	}
	public List<IsoDocument> getLstTemplates() {
		return lstTemplates;
	}
	public void setLstTemplates(List<IsoDocument> lstTemplates) {
		this.lstTemplates = lstTemplates;
	}
	public MainCategoryType getType() {
		return type;
	}
	public void setType(MainCategoryType type) {
		this.type = type;
	}
	public List<File> getLstTemplateDefault() {
		return lstTemplateDefault;
	}

	public void setLstTemplateDefault(List<File> lstTemplateDefault) {
		this.lstTemplateDefault = lstTemplateDefault;
	}
	
	
}
