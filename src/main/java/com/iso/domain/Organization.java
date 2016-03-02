package com.iso.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.iso.domain.base.BaseDomain;

@Document
public class Organization extends BaseDomain{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private ObjectId id;
	@Field 
	private String code;
	@Field
	private String name;
	@Field
	private String description;
	@Field
	private boolean active;
	@Field
	private boolean ownerOrganization;
	@Field
	private boolean isLocked;
	
	@Transient
	private String status;
	@Transient
	private Category rootCategory;
	
	public Organization() {
		active = true;
		ownerOrganization = false;
		isLocked = false;
	};
	
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
	public boolean getActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isOwnerOrganization() {
		return ownerOrganization;
	}
	public void setOwnerOrganization(boolean ownerOrganization) {
		this.ownerOrganization = ownerOrganization;
	}
	public boolean isLocked() {
		return isLocked;
	}
	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Category getRootCategory() {
		return rootCategory;
	}
	public void setRootCategory(Category rootCategory) {
		this.rootCategory = rootCategory;
	}	
	
}
