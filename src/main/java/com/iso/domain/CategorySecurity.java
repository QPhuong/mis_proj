package com.iso.domain;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.iso.domain.base.BaseDomain;


@Document
public class CategorySecurity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@DBRef
	private OrganizationUnit orgUnit;
	@DBRef
	private User user;
	
	private boolean canViewCategory;
	private boolean canAddCategory;
	private boolean canDeleteCategory;
	private boolean canEditCategory;
	private boolean canMoveCategory;
	private boolean canSetupSecurity;
	private boolean canUploadDoc;
	
	public CategorySecurity() {
		this.permitSetup(false);
	}

	public CategorySecurity(boolean permit) {
		this.permitSetup(permit);
	}

	private void permitSetup(boolean permit) {
		this.canViewCategory = permit;
		this.canAddCategory = permit;
		this.canDeleteCategory = permit;
		this.canMoveCategory = permit;
		this.canEditCategory = permit;
		this.canSetupSecurity = permit;
		this.canUploadDoc = permit;
	}
	public OrganizationUnit getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(OrganizationUnit orgUnit) {
		this.orgUnit = orgUnit;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isCanAddCategory() {
		return canAddCategory;
	}

	public void setCanAddCategory(boolean canAddCategory) {
		this.canAddCategory = canAddCategory;
	}

	public boolean isCanDeleteCategory() {
		return canDeleteCategory;
	}

	public void setCanDeleteCategory(boolean canDeleteCategory) {
		this.canDeleteCategory = canDeleteCategory;
	}

	public boolean isCanEditCategory() {
		return canEditCategory;
	}

	public void setCanEditCategory(boolean canEditCategory) {
		this.canEditCategory = canEditCategory;
	}

	public boolean isCanMoveCategory() {
		return canMoveCategory;
	}

	public void setCanMoveCategory(boolean canMoveCategory) {
		this.canMoveCategory = canMoveCategory;
	}

	public boolean isCanSetupSecurity() {
		return canSetupSecurity;
	}

	public void setCanSetupSecurity(boolean canSetupSecurity) {
		this.canSetupSecurity = canSetupSecurity;
	}

	public boolean isCanViewCategory() {
		return canViewCategory;
	}

	public void setCanViewCategory(boolean canViewCategory) {
		this.canViewCategory = canViewCategory;
	}
	
	public boolean isCanUploadDoc() {
		return canUploadDoc;
	}

	public void setCanUploadDoc(boolean canUploadDoc) {
		this.canUploadDoc = canUploadDoc;
	}

	public CategorySecurity cloneCategorySecurity() {
		CategorySecurity security = new CategorySecurity();
		security.canAddCategory = this.canAddCategory;
		security.canDeleteCategory = this.canDeleteCategory;
		security.canMoveCategory = this.canMoveCategory;
		security.canEditCategory = this.canEditCategory;
		security.canSetupSecurity = this.canSetupSecurity;
		security.canViewCategory = this.canViewCategory;
		security.canUploadDoc = this.canUploadDoc;
		security.orgUnit = this.orgUnit;
		security.user = this.user;
		return security;
	}
}
