package com.iso.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.iso.domain.base.BaseDomain;
import com.iso.domain.generic.Metadata;

/**
 * @author Phuong
 *
 */
@Document
public class Category extends BaseDomain {

	private static final long serialVersionUID = 1L;

	@Id
	private ObjectId id;
	@Field
	@TextIndexed(weight=1)
	private String code;
	@Field
	@TextIndexed(weight=2)
	private String name;
	@Field
	@TextIndexed
	private String description;
	@DBRef
	private Category parent;
	@DBRef
	private Organization organization;
	@Field
	private List<CategorySecurity> categorySecurities;
	@Field
	@TextIndexed
	private List<Metadata<?>> properties;
	@DBRef
	private List<IsoDocument> lstTemplates;
	@Field 
	private boolean locked;
	@Field 
	private boolean deleted;
	@Field
	private boolean root;
		
	@Transient
	private String path;
	@Transient
	private long subCatNum;
	@Transient
	private long fileNum;
	@Transient
	private CategorySecurity finalSecurity;
	
	public Category(String code, String name, String description) {
		this.code = code;
		this.name = name;
		this.description = description;
		this.locked = false;
		this.categorySecurities = new ArrayList<CategorySecurity>();
		this.lstTemplates = new ArrayList<IsoDocument>();
	}
	public Category(String code, String name, String description, boolean locked) {
		this.code = code;
		this.name = name;
		this.description = description;
		this.locked = locked;
		this.categorySecurities = new ArrayList<CategorySecurity>();
		this.lstTemplates = new ArrayList<IsoDocument>();
	}
	public Category(MainCategory mainCategory) {
		this.code = mainCategory.getCode();
		this.name = mainCategory.getName();
		this.description = mainCategory.getDescription();
		this.locked = false;
		this.categorySecurities = new ArrayList<CategorySecurity>();
		this.lstTemplates = new ArrayList<IsoDocument>();
	}
	
	public List<Metadata<?>> getProperties() {
		return properties;
	}
	public void setProperties(List<Metadata<?>> properties) {
		this.properties = properties;
	}
	public CategorySecurity getFinalSecurity() {
		return finalSecurity;
	}
	public void setFinalSecurity(CategorySecurity finalSecurity) {
		this.finalSecurity = finalSecurity;
	}
	public Category() {
		this.categorySecurities = new ArrayList<CategorySecurity>();
	}
	public List<CategorySecurity> getCategorySecurities() {
		return categorySecurities;
	}
	public void setCategorySecurities(List<CategorySecurity> categorySecurities) {
		this.categorySecurities = categorySecurities;
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Category getParent() {
		return parent;
	}
	public void setParent(Category parent) {
		this.parent = parent;
	}
	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public long getSubCatNum() {
		return subCatNum;
	}
	public void setSubCatNum(long subCatNum) {
		this.subCatNum = subCatNum;
	}
	public long getFileNum() {
		return fileNum;
	}
	public void setFileNum(long fileNum) {
		this.fileNum = fileNum;
	}
	public List<IsoDocument> getLstTemplates() {
		return lstTemplates;
	}
	public void setLstTemplates(List<IsoDocument> lstTemplates) {
		this.lstTemplates = lstTemplates;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public boolean isRoot() {
		return root;
	}
	public void setRoot(boolean root) {
		this.root = root;
	}
	
}
