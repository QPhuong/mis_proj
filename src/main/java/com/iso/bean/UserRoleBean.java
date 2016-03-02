package com.iso.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.dao.DataAccessException;

import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.domain.UserRole;
import com.iso.exception.BusinessException;
import com.iso.service.OrganizationService;
import com.iso.service.UserRoleService;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="userRoleBean")
@ViewScoped
public class UserRoleBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private UserRole selectedUserRole;
	private List<UserRole> userRoleLst;
	private List<UserRole> userRoleFilterLst;
	
	private String filterText;
	
	private List<Organization> organizationLst;
	private Organization selectedOrganization;
	
	private boolean isShownDetail = false;
	private boolean isMultiOrganization;
	
	public UserRoleBean(){
		selectedUserRole = new UserRole();
	}
	
	@ManagedProperty(value="#{userRoleService}")
	private UserRoleService userRoleService;	

	@ManagedProperty(value="#{organizationService}")
	private OrganizationService organizationService;
	
	public UserRole getSelectedUserRole() {
		return selectedUserRole;
	}

	public void setSelectedUserRole(UserRole selectedUserRole) {
		this.selectedUserRole = selectedUserRole;
	}

	public List<UserRole> getUserRoleLst() {
		return userRoleLst;
	}

	public void setUserRoleLst(List<UserRole> userRoleLst) {
		this.userRoleLst = userRoleLst;
		resetFilter();
	}

	public UserRoleService getUserRoleService() {
		return userRoleService;
	}

	public void setUserRoleService(UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}

	public boolean isShownDetail() {
		return isShownDetail;
	}

	public void setShownDetail(boolean isShownDetail) {
		this.isShownDetail = isShownDetail;
	}

	public List<Organization> getOrganizationLst() {
		return organizationLst;
	}

	public void setOrganizationLst(List<Organization> organizationLst) {
		this.organizationLst = organizationLst;
	}

	public Organization getSelectedOrganization() {
		return selectedOrganization;
	}

	public void setSelectedOrganization(Organization selectedOrganization) {
		this.selectedOrganization = selectedOrganization;
	}

	public OrganizationService getOrganizationService() {
		return organizationService;
	}

	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}
	public boolean isMultiOrganization() {
		return isMultiOrganization;
	}

	public void setMultiOrganization(boolean isMultiOrganization) {
		this.isMultiOrganization = isMultiOrganization;
	}

	public List<UserRole> getUserRoleFilterLst() {
		return userRoleFilterLst;
	}

	public void setUserRoleFilterLst(List<UserRole> userRoleFilterLst) {
		this.userRoleFilterLst = userRoleFilterLst;
	}

	public String getFilterText() {
		return filterText;
	}

	public void setFilterText(String filterText) {
		this.filterText = filterText;
	}

	public boolean isEnableAddRole() {
		return (this.selectedOrganization != null && !StringUtils.isEmpty(this.selectedOrganization.getCode()));
	}
	
	@PostConstruct
	public void initialize() {
		try{
			User loginUser = FaceContextUtils.getLoginUser();
			Object bean = FaceContextUtils.getApplicationScopeBean("applicationSetupBean");
			if (bean != null) {
				ApplicationSetupBean appSetupBean = (ApplicationSetupBean) bean;
			    isMultiOrganization = appSetupBean.isMultiOrganizationSetup();
			}
			
			//only show multi organization selection for system admin and with multi organization application setup
			isMultiOrganization = isMultiOrganization && loginUser.isSysAdministrator();
			
			if(!isMultiOrganization) {
				this.selectedOrganization = loginUser.getOrganization();
			} else {
				this.organizationLst = organizationService.getActiveOrganization();
				this.selectedOrganization = (CollectionUtils.isNotEmpty(this.organizationLst) ? this.organizationLst.get(0) : null);
			}
			doLoadUserRoleLst();
			
		}catch(DataAccessException e) {
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error when search user role!",null));
		}
	}
	
	private void resetFilter() {
		this.userRoleFilterLst = null;
		this.filterText = null;
		RequestContext.getCurrentInstance().execute("if (typeof PF('userRoleWidgetVar') !== 'undefined') { PF('userRoleWidgetVar').clearFilters();}");
		
	}
	
	/**
	 * Save (new and update) user role detail
	 * @param event
	 */
	public void  doUserRoleDetailSave(ActionEvent event) {
		try {
			if (this.selectedUserRole.getOrganization() == null) {
				this.selectedUserRole.setOrganization(this.selectedOrganization);
			}
			userRoleService.saveUserRoleData(FaceContextUtils.getLoginUser(), selectedUserRole);
			
			doLoadUserRoleLst();
			
			FaceContextUtils.showInfoMessage("Save Successfully!", null);
			
		}catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when saving user role!",null);
			
		} catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), null);
		}
	}

	/**
	 * Delete user role
	 * @param event
	 */
	public void doUserRoleDelete(ActionEvent event) {
		try {
			userRoleService.delete(FaceContextUtils.getLoginUser(), selectedUserRole);
			doLoadUserRoleLst();
			
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage("Delete Successfully!"));
		}catch (Exception e) {	
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error when delete user role!",null));
			
		}
	}
	
	/**
	 * Edit User Role detail
	 */
	public void doUserRoleDetailEdit() {
		setShownDetail(true);
	}
	
	/**
	 * Cancel user role editing
	 */
	public void doUserRoleDetailCancel(){
		selectedUserRole = new UserRole();
		setShownDetail(false);
	}
	
	/**
	 * Add new user role
	 */
	public void doAddNewUserRole() {
		setShownDetail(true);
		this.selectedUserRole = new UserRole();
	}
	
	/**
	 * Search Organization by name in autocomplete
	 * @param orgName
	 * @return
	 */
	public List<Organization> doSearchOrganization(String orgName) {
		this.organizationLst = organizationService.searchOrganozationByName(orgName);
		return this.organizationLst;
	}
	
	/**
	 * load all user role of selected Organization
	 * @param event
	 */
	public void doLoadUserRoleLst() {
		try{
			setUserRoleLst(userRoleService.getUserRoleByOrganization(this.selectedOrganization));
			this.selectedUserRole = new UserRole();
			this.isShownDetail = false;
			
		}catch(DataAccessException e) {
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error when search user role!",null));
		}
	}
	
}
