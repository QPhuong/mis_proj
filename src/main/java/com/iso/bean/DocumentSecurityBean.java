package com.iso.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.iso.domain.DocumentSecurity;
import com.iso.domain.IsoDocument;
import com.iso.domain.Organization;
import com.iso.domain.OrganizationUnit;
import com.iso.domain.User;
import com.iso.service.IsoDocumentService;
import com.iso.service.OrganizationUnitService;
import com.iso.service.UserService;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="documentSecurityBean")
@ViewScoped
public class DocumentSecurityBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private DocumentSecurity selectedSecurity;
	private Organization organization;
	private IsoDocument selectedDoc;
	
	private OrganizationUnit selectedOrgUnit;
	private User selectedUser;
	
	@ManagedProperty(value="#{organizationUnitService}")
	private OrganizationUnitService organizationUnitService;
	
	@ManagedProperty(value="#{userService}")
	private UserService userService;
	
	@ManagedProperty(value="#{isoDocumentService}")
	private IsoDocumentService isoDocumentService;
	
	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	public IsoDocument getSelectedDoc() {
		return selectedDoc;
	}
	public void setSelectedDoc(IsoDocument selectedDoc) {
		this.selectedDoc = selectedDoc;
	}
	public OrganizationUnit getSelectedOrgUnit() {
		return selectedOrgUnit;
	}
	public void setSelectedOrgUnit(OrganizationUnit selectedOrgUnit) {
		this.selectedOrgUnit = selectedOrgUnit;
	}
	public User getSelectedUser() {
		return selectedUser;
	}
	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}
	public OrganizationUnitService getOrganizationUnitService() {
		return organizationUnitService;
	}
	public void setOrganizationUnitService(
			OrganizationUnitService organizationUnitService) {
		this.organizationUnitService = organizationUnitService;
	}
	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	public IsoDocumentService getIsoDocumentService() {
		return isoDocumentService;
	}
	public void setIsoDocumentService(IsoDocumentService isoDocumentService) {
		this.isoDocumentService = isoDocumentService;
	}
	public DocumentSecurity getSelectedSecurity() {
		return selectedSecurity;
	}
	public void setSelectedSecurity(DocumentSecurity selectedSecurity) {
		this.selectedSecurity = selectedSecurity;
	}
	public List<DocumentSecurity> getSecurityLst() {
		return (this.selectedDoc == null || CollectionUtils.isEmpty(this.selectedDoc.getDocumentSecurities())) 
				? new ArrayList<DocumentSecurity>() : this.selectedDoc.getDocumentSecurities();
	}
	public boolean isSelectedDocument() {
		return (this.selectedDoc != null && this.selectedDoc.getId() != null);
	}
	
	@PostConstruct
	public void initialize() {
		try{
			Organization organization = FaceContextUtils.getLoginUser().getOrganization();
			this.organization = organization;
			
			doReloadDocumentSecurity();
			
		}catch(Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when getting document security information", e.getStackTrace().toString());
		}
	}
	
	public void doReloadDocumentSecurity() {
		if (FaceContextUtils.getViewScopeBean("documentBean") != null) {
			DocumentBean documentBeanBean = (DocumentBean) FaceContextUtils.getViewScopeBean("documentBean");
			this.selectedDoc = documentBeanBean.getSelectedDoc();
			this.selectedDoc = isoDocumentService.getIsoDocumentAndSecurityById(this.selectedDoc.getId().toString(), FaceContextUtils.getLoginUser());
		} else {
			this.selectedDoc = null;
		}
	}
	
	public List<OrganizationUnit> doSearchOrganizationUnit(String searchText) {
		return organizationUnitService.searchByName(this.organization, StringUtils.trim(searchText));
	}
	
	public List<User> doSearchUser(String searchText) {
		return userService.searchByName(this.organization, searchText);
	}
	
	private void saveSingleDocumentSecurity(IsoDocument document, DocumentSecurity security, OrganizationUnit orgUnit, User user, boolean isOverride) {
		
		boolean isNew = true;
		if (CollectionUtils.isEmpty(document.getDocumentSecurities())) {
			document.setDocumentSecurities(new ArrayList<DocumentSecurity>());
		}
		for(int index = 0; index < document.getDocumentSecurities().size(); index++) {
			DocumentSecurity existing = document.getDocumentSecurities().get(index);
			
			if (existing.getOrgUnit() != null && orgUnit != null && 
					orgUnit.getId().equals(existing.getOrgUnit().getId())) {
				isNew = false;
				if (isOverride) {
					document.getDocumentSecurities().set(index, security);
				}
				break;
			}else if (existing.getUser() != null && user != null && 
					user.getId().equals(existing.getUser().getId())) {				
				isNew = false;
				if (isOverride) {
					document.getDocumentSecurities().set(index, security);
				}
				break;
			}
		}
		if (isNew) {
			security.setOrgUnit(orgUnit);
			security.setUser(user);
			document.getDocumentSecurities().add(security);
		}
		isoDocumentService.saveDocumentSecurity(FaceContextUtils.getLoginUser(), document);
	}
	
	private void addAllSubOrgUnitDocumentSecurity(DocumentSecurity security, OrganizationUnit orgUnit) {
		
		this.saveSingleDocumentSecurity(this.selectedDoc, security, orgUnit, null, false);
		
		List<OrganizationUnit> subOrgUnits = organizationUnitService.getOrganizationChildByParent(this.organization, orgUnit);
		if (CollectionUtils.isNotEmpty(subOrgUnits)) {
			for (OrganizationUnit subOrgUnit: subOrgUnits) {
				addAllSubOrgUnitDocumentSecurity(security.cloneDocumentSecurity(), subOrgUnit);
			}
		}
		
	}

	/**
	 * Add a new document security with empty permissions
	 * @param event
	 */
	public void doAddNewDocumentSecurity(ActionEvent event) {
		try {
			this.saveSingleDocumentSecurity(this.selectedDoc, new DocumentSecurity(), this.selectedOrgUnit, this.selectedUser, false);
			this.selectedOrgUnit = null;
			this.selectedUser = null;
			
			FaceContextUtils.showInfoMessage("Add document security successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when adding document security information", e.getStackTrace().toString());
		}		
	}	
	
	/**
	 * Save all document security permission in screen
	 */
	public void doSaveAllDocumentSecurity() {
		try {
			isoDocumentService.saveDocumentSecurity(FaceContextUtils.getLoginUser(), this.selectedDoc);
			FaceContextUtils.showInfoMessage("Save document security successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when saving document security information", e.getStackTrace().toString());
		}
	}
	
	/**
	 * Reset all document security in screen to original
	 */
	public void doResetAllDocumentSecurity() {
		try {
			this.selectedDoc = isoDocumentService.getIsoDocumentAndSecurityById(this.selectedDoc.getId().toString(), FaceContextUtils.getLoginUser());
			FaceContextUtils.showInfoMessage("Reset document security successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when reset document security information", e.getStackTrace().toString());
		}
	}
	
	/**
	 * Delete a document security
	 */
	public void doDeleteDocumentSecurity() {
		try {
			this.selectedDoc.getDocumentSecurities().remove(this.selectedSecurity);
			isoDocumentService.saveDocumentSecurity(FaceContextUtils.getLoginUser(), this.selectedDoc);
			
			FaceContextUtils.showInfoMessage("Reset document security successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when delete document security information", e.getStackTrace().toString());
		}
	}
	
	/**
	 * Add category document for an organization unit and all sub organization unit
	 */
	public void doAddAllSubOrgUnitDocumentSecurity() {
		try {
			addAllSubOrgUnitDocumentSecurity(new DocumentSecurity(), this.selectedOrgUnit);
			
			this.selectedOrgUnit = null;
			this.selectedUser = null;
			
			FaceContextUtils.showInfoMessage("Add document security successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when adding document security information", e.getStackTrace().toString());
		}
	}
	
	private void applyInherit(DocumentSecurity security, OrganizationUnit orgUnit) {
		
		OrganizationUnit parent = orgUnit.getParent();
		
		if (CollectionUtils.isNotEmpty(this.selectedDoc.getDocumentSecurities())) {
			for (DocumentSecurity existing: this.selectedDoc.getDocumentSecurities()) {
				if (existing.getOrgUnit() != null && parent != null &&
						existing.getOrgUnit().getId().equals(parent.getId())) {
					security = existing.cloneDocumentSecurity();
					break;
				}
			}
		}
		security.setOrgUnit(orgUnit);
		this.saveSingleDocumentSecurity(this.selectedDoc, security, orgUnit, null, true);
	}
	
	public void doAddDocumentSecurityInherit() {
		try {
			DocumentSecurity subSecurity = new DocumentSecurity();
			subSecurity.setOrgUnit(this.selectedOrgUnit);
			
			applyInherit(subSecurity, this.selectedOrgUnit);
			
			this.selectedOrgUnit = null;
			this.selectedUser = null;
			
			FaceContextUtils.showInfoMessage("Add document security successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when adding document security information", e.getStackTrace().toString());
		}
	}
	
	public void doApplyInheritSecurityFromParent() {
		try {
			applyInherit(this.selectedSecurity, this.selectedSecurity.getOrgUnit());
			this.selectedOrgUnit = null;
			this.selectedUser = null;
			
			FaceContextUtils.showInfoMessage("Save document security successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when adding document security information", e.getStackTrace().toString());
		}
	}
	
	public void doApplyToAllSubOrgUnit() {
		try {
			if (CollectionUtils.isNotEmpty(this.selectedDoc.getDocumentSecurities())) {				
				for(int index = 0; index < this.selectedDoc.getDocumentSecurities().size(); index++) {
					
					DocumentSecurity security = this.selectedDoc.getDocumentSecurities().get(index);
					if (security.getOrgUnit() != null && security.getOrgUnit().getParent() != null &&
							security.getOrgUnit().getParent().getId().equals(this.selectedSecurity.getOrgUnit().getId())) {
						DocumentSecurity clone = this.selectedSecurity.cloneDocumentSecurity();
						clone.setOrgUnit(security.getOrgUnit());
						this.selectedDoc.getDocumentSecurities().set(index, clone);
					}
				}
			}
			
			isoDocumentService.saveDocumentSecurity(FaceContextUtils.getLoginUser(), this.selectedDoc);
			
			this.selectedOrgUnit = null;
			this.selectedUser = null;
			
			FaceContextUtils.showInfoMessage("Add document security successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when adding document security information", e.getStackTrace().toString());
		}
	}
	
}
