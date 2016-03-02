package com.iso.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.iso.domain.base.BaseDomain;

@Document
public class UserRole extends BaseDomain {

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
	
	@Field(value="SystemAdminRole")
	private boolean isSystemAdminRole;
	
	@Field(value="OrganizationAdminRole")
	private boolean isOrganizationAdminRole;
	@DBRef
	private Organization organization;
	@Field(value="MenuSecurites")
	private List<Menu> menuLst;
	
	public List<Menu> getMenuLst() {
		return menuLst;
	}
	public void setMenuLst(List<Menu> menuLst) {
		this.menuLst = menuLst;
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
	public boolean isSystemAdminRole() {
		return isSystemAdminRole;
	}
	public void setSystemAdminRole(boolean isSystemAdminRole) {
		this.isSystemAdminRole = isSystemAdminRole;
	}
	public boolean isOrganizationAdminRole() {
		return isOrganizationAdminRole;
	}
	public void setOrganizationAdminRole(boolean isOrganizationAdminRole) {
		this.isOrganizationAdminRole = isOrganizationAdminRole;
	}
	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
}
