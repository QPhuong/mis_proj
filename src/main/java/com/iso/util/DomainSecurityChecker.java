package com.iso.util;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.iso.domain.Category;
import com.iso.domain.CategorySecurity;
import com.iso.domain.DocumentSecurity;
import com.iso.domain.IsoDocument;
import com.iso.domain.OrganizationUnit;
import com.iso.domain.User;

public final class DomainSecurityChecker {

	private static CategorySecurity checkCategorySecurity(User user, List<CategorySecurity> categorySecurities) {
		
		CategorySecurity finalSecurity = new CategorySecurity();
		
		if (CollectionUtils.isNotEmpty(categorySecurities)) {
			for(CategorySecurity security : categorySecurities) {
				//check security of user
				if (security.getUser() != null && security.getUser().getId().equals(user.getId())) {
					return security;
				}
				//check security of organization unit of user
				if (CollectionUtils.isNotEmpty(user.getOrganizationUnits())) {
					for (OrganizationUnit orgUnit : user.getOrganizationUnits()) {
						if (security.getOrgUnit() != null && security.getOrgUnit().getId().equals(orgUnit.getId())) {
							finalSecurity.setCanAddCategory(finalSecurity.isCanAddCategory() || security.isCanAddCategory());
							finalSecurity.setCanDeleteCategory(finalSecurity.isCanDeleteCategory() || security.isCanDeleteCategory());
							finalSecurity.setCanMoveCategory(finalSecurity.isCanMoveCategory() || security.isCanMoveCategory());
							finalSecurity.setCanEditCategory(finalSecurity.isCanEditCategory() || security.isCanEditCategory());
							finalSecurity.setCanSetupSecurity(finalSecurity.isCanSetupSecurity() || security.isCanSetupSecurity());
							finalSecurity.setCanViewCategory(finalSecurity.isCanViewCategory() || security.isCanViewCategory());
							finalSecurity.setCanUploadDoc(finalSecurity.isCanUploadDoc() || security.isCanUploadDoc());
							break;
						}
					}
				}
			}
		}
		
		return finalSecurity;
	}
	
	private static DocumentSecurity checkDocumentSecurity(User user, List<DocumentSecurity> documentSecurities) {
		
		DocumentSecurity finalSecurity = new DocumentSecurity();
		
		if (CollectionUtils.isNotEmpty(documentSecurities)) {
			
			for(DocumentSecurity security : documentSecurities) {
				
				//check security of user
				if (security.getUser() != null && security.getUser().getId().equals(user.getId())) {
					return security;
				}
				
				//check security of organization unit of user
				if (CollectionUtils.isNotEmpty(user.getOrganizationUnits())) {
					
					for (OrganizationUnit orgUnit : user.getOrganizationUnits()) {
					
						if (security.getOrgUnit() != null && security.getOrgUnit().getId().equals(orgUnit.getId())) {
							finalSecurity.setCanViewDocInfo(finalSecurity.isCanViewDocInfo() || security.isCanViewDocInfo());
							finalSecurity.setCanDownloadDoc(finalSecurity.isCanDownloadDoc() || security.isCanDownloadDoc());
							finalSecurity.setCanEditDoc(finalSecurity.isCanEditDoc() || security.isCanEditDoc());
							finalSecurity.setCanDeleteDoc(finalSecurity.isCanDeleteDoc() || security.isCanDeleteDoc());
							finalSecurity.setCanSetupSecurity(finalSecurity.isCanSetupSecurity() || security.isCanSetupSecurity());
							break;
						}
					}
				}
			}
		}
		
		return finalSecurity;
	}

	public static CategorySecurity checkCategorySecurity(User user, Category category) {
		CategorySecurity security = null;
		if (category != null && category.getId() != null) {
			security = DomainSecurityChecker.checkCategorySecurity(user, category.getCategorySecurities());
			if (user.isOrganizationAdministrator()) {
				security.setCanViewCategory(true);
				security.setCanSetupSecurity(true);
			}
		}
		return security;
	}
	
	public static DocumentSecurity checkDocumentSecurity(User user, IsoDocument document) {
		DocumentSecurity security = null;
		if (document != null && document.getId() != null) {
			security = DomainSecurityChecker.checkDocumentSecurity(user, document.getDocumentSecurities());
			if (user.isOrganizationAdministrator()) {
				security.setCanViewDocInfo(true);
				security.setCanSetupSecurity(true);
			}
		}
		return security;
	}
	
}
