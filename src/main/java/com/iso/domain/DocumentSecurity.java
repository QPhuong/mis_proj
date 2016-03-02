package com.iso.domain;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class DocumentSecurity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@DBRef
	private OrganizationUnit orgUnit;
	@DBRef
	private User user;
		
	private boolean canSetupSecurity;
	private boolean canViewDocInfo;
	private boolean canEditDoc;
	private boolean canDownloadDoc;	
	private boolean canDeleteDoc;
	
	public DocumentSecurity() {
		this.permitSetup(false);
	}

	public DocumentSecurity(boolean permit) {
		this.permitSetup(permit);
	}

	private void permitSetup(boolean permit) {
		this.canSetupSecurity = permit;
		this.canViewDocInfo = permit;
		this.canDownloadDoc = permit;
		this.canEditDoc = permit;
		this.canDeleteDoc = permit;
	}

	public DocumentSecurity cloneDocumentSecurity() {
		DocumentSecurity security = new DocumentSecurity();
		security.canDownloadDoc = this.canDownloadDoc;
		security.canViewDocInfo = this.canViewDocInfo;
		security.canEditDoc = this.canEditDoc;
		security.canSetupSecurity = this.canSetupSecurity;
		security.canDeleteDoc = this.canDeleteDoc;
		security.orgUnit = this.orgUnit;
		security.user = this.user;
		return security;
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

	public boolean isCanSetupSecurity() {
		return canSetupSecurity;
	}

	public void setCanSetupSecurity(boolean canSetupSecurity) {
		this.canSetupSecurity = canSetupSecurity;
	}

	public boolean isCanViewDocInfo() {
		return canViewDocInfo;
	}

	public void setCanViewDocInfo(boolean canViewDocInfo) {
		this.canViewDocInfo = canViewDocInfo;
	}

	public boolean isCanEditDoc() {
		return canEditDoc;
	}

	public void setCanEditDoc(boolean canEditDoc) {
		this.canEditDoc = canEditDoc;
	}

	public boolean isCanDownloadDoc() {
		return canDownloadDoc;
	}

	public void setCanDownloadDoc(boolean canDownloadDoc) {
		this.canDownloadDoc = canDownloadDoc;
	}

	public boolean isCanDeleteDoc() {
		return canDeleteDoc;
	}

	public void setCanDeleteDoc(boolean canDeleteDoc) {
		this.canDeleteDoc = canDeleteDoc;
	}
	
	
}
