package com.iso.model;

import java.io.Serializable;
import java.util.Date;

import com.iso.domain.Category;
import com.iso.domain.IsoDocument;
import com.iso.domain.User;
import com.iso.util.DomainSecurityChecker;
import com.iso.util.IsoFileSupportUtils;

public class IsoTrashDomain implements Serializable{

	private static final long serialVersionUID = 1L;

	private String name;
	private String originalLocation;
	private Date dateDeleted;
	private Date dateModified;
	private User deletedBy;
	private long size;
	private String itemType;
	private String itemIcon;
	
	private boolean canDeletePermanent;
	private boolean canRestore;
	
	private Object domain;

	public IsoTrashDomain() {
	}
	
	public IsoTrashDomain(Category category, User loginUser) {
		this.name = category.getName();
		this.originalLocation = IsoFileSupportUtils.getLocation(category);
		this.dateDeleted = category.getDeletedOn();
		this.deletedBy = category.getDeletedBy();
		this.dateModified = category.getUpdatedOn();
		this.itemType = "Category";
		this.itemIcon = "category";
		this.domain = category;
		if (category.getFinalSecurity() == null) {
			category.setFinalSecurity(DomainSecurityChecker.checkCategorySecurity(loginUser, category));
		}
		this.canRestore = category.getFinalSecurity().isCanDeleteCategory();
		this.canDeletePermanent = loginUser.isOrganizationAdministrator();
	}
	
	public IsoTrashDomain(IsoDocument document, User loginUser) {
		this.name = document.getFileTitle();
		this.originalLocation = IsoFileSupportUtils.getLocation(document);
		this.dateDeleted = document.getDeletedOn();
		this.deletedBy = document.getDeletedBy();
		this.dateModified = document.getUpdatedOn();
		this.size = document.getSize();
		this.itemType = document.getExtension();
		this.itemIcon = "document";
		this.domain = document;
		if (document.getFinalSecurity() == null) {
			document.setFinalSecurity(DomainSecurityChecker.checkDocumentSecurity(loginUser, document));
		}
		this.canRestore = document.getFinalSecurity().isCanDeleteDoc();
		this.canDeletePermanent = loginUser.isOrganizationAdministrator();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOriginalLocation() {
		return originalLocation;
	}

	public void setOriginalLocation(String originalLocation) {
		this.originalLocation = originalLocation;
	}

	public Date getDateDeleted() {
		return dateDeleted;
	}

	public void setDateDeleted(Date dateDeleted) {
		this.dateDeleted = dateDeleted;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getItemIcon() {
		return itemIcon;
	}

	public void setItemIcon(String itemIcon) {
		this.itemIcon = itemIcon;
	}

	public Object getDomain() {
		return domain;
	}

	public void setDomain(Object domain) {
		this.domain = domain;
	}

	public User getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(User deletedBy) {
		this.deletedBy = deletedBy;
	}

	public boolean isCanDeletePermanent() {
		return canDeletePermanent;
	}

	public void setCanDeletePermanent(boolean canDeletePermanent) {
		this.canDeletePermanent = canDeletePermanent;
	}

	public boolean isCanRestore() {
		return canRestore;
	}

	public void setCanRestore(boolean canRestore) {
		this.canRestore = canRestore;
	}
	
	
}
