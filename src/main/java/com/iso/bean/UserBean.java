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
import org.primefaces.event.TabChangeEvent;
import org.springframework.dao.DataAccessException;

import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.exception.BusinessException;
import com.iso.service.OrganizationService;
import com.iso.service.UserService;
import com.iso.util.FaceContextUtils;
import com.iso.util.HashCode;

@ManagedBean(name="userBean")
@ViewScoped
public class UserBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private User selectedUser;
	private List<User> userLst;
	private List<User> userFilterLst;
	
	private String filterText;
	
	private List<Organization> organizationLst;
	private Organization selectedOrganization;
	
	private boolean isShownDetail = false;
	
	private boolean isMultiOrganization;
	
	public UserBean(){
		selectedUser = new User();
	}
	
	@ManagedProperty(value="#{userService}")
	private UserService userService;	

	@ManagedProperty(value="#{organizationService}")
	private OrganizationService organizationService;
	
	public User getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(User selectedUser) {		
		this.selectedUser = selectedUser;		
	}

	public List<User> getUserLst() {
		return userLst;
	}

	public void setUserLst(List<User> userLst) {
		this.userLst = userLst;
		resetFilter();
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public boolean isShownDetail() {
		return isShownDetail;
	}

	public void setShownDetail(boolean isShownDetail) {
		this.isShownDetail = isShownDetail;
	}

	public boolean isMultiOrganization() {
		return isMultiOrganization;
	}

	public void setMultiOrganization(boolean isMultiOrganization) {
		this.isMultiOrganization = isMultiOrganization;
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

	public List<User> getUserFilterLst() {
		return userFilterLst;
	}

	public void setUserFilterLst(List<User> userFilterLst) {
		this.userFilterLst = userFilterLst;
	}

	public String getFilterText() {
		return filterText;
	}

	public void setFilterText(String filterText) {
		this.filterText = filterText;
	}

	public boolean isShownAddUser() {
		return (this.selectedOrganization != null && !StringUtils.isEmpty(this.selectedOrganization.getCode()));
	}
	
	@PostConstruct
	public void initialize() {
		try{
			Object bean = FaceContextUtils.getApplicationScopeBean("applicationSetupBean");
			if (bean != null) {
				ApplicationSetupBean appSetupBean = (ApplicationSetupBean) bean;
			    isMultiOrganization = appSetupBean.isMultiOrganizationSetup();
			}
			
			User loginUser = FaceContextUtils.getLoginUser();
			
			//only show multi organization selection for system admin and with multi organization application setup
			isMultiOrganization = isMultiOrganization && loginUser.isSysAdministrator();
			
			if(!isMultiOrganization) {
				this.selectedOrganization = loginUser.getOrganization();
			}else {
				this.organizationLst = organizationService.getActiveOrganization();
				this.selectedOrganization = (CollectionUtils.isNotEmpty(this.organizationLst) ? this.organizationLst.get(0) : null);
			}
			doLoadUserLst();
		}catch(DataAccessException e) {
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error when search user!",null));
		}
	}
	
	private void resetFilter() {
		this.filterText = "";
		this.userFilterLst = null;
		RequestContext.getCurrentInstance().execute("if (typeof PF('userWidgetVar') !== 'undefined') { PF('userWidgetVar').clearFilters();}");
	}
	
	/**
	 * Save (new and update) user profile
	 * @param event
	 */
	public void  doUserDetailSave(ActionEvent event) {
		try {
			if (this.selectedUser.getOrganization() == null) {
				this.selectedUser.setOrganization(this.selectedOrganization);
			}
			if (this.selectedUser.getId() == null) {
				this.selectedUser.setPassword(HashCode.getHashPassword(this.selectedUser.getPassword()));
			}
			userService.saveUserProfile(selectedUser, FaceContextUtils.getLoginUser());
			
			doLoadUserLst();
			
			FaceContextUtils.showInfoMessage("Save Successfully!", null);
		}catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when saving user profile!", null);
		} catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), null);
		}
	}
	
	/**
	 * Delete user
	 * @param event
	 */
	public void doUserDelete(ActionEvent event) {
		try {
			userService.delete(selectedUser.getId().toString(), FaceContextUtils.getLoginUser());
			doLoadUserLst();
			
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage("Delete Successfully!"));
		}catch (Exception e) {	
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error when delete user profile!",null));
			
		}
	}
	
	public void doUserDetailEdit() {
		setShownDetail(true);
	}
	
	public void doUserDetailCancel(){
		selectedUser = new User();
		setShownDetail(false);
	}
	
	public void doAddNewUser() {
		setShownDetail(true);
		this.selectedUser = new User();
	}
	
	public List<Organization> doSearchOrganization(String organizationName) {
		this.organizationLst = organizationService.searchOrganozationByName(organizationName);
		return this.organizationLst;
	}
	
	public void doLoadUserLst() {
		try{
			setUserLst(userService.getUserLstByOrganization(this.selectedOrganization));
			selectedUser = new User();			
			setShownDetail(false);

		}catch(DataAccessException e) {
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error when search user!",null));
		}
	}
	
	public void onTabChange(TabChangeEvent event) {
		String tabID = event.getTab().getClientId();
		if (!StringUtils.isEmpty(tabID) && tabID.contains("userRoleAssignmentTab")) {
			UserRoleAssignmentBean roleAssignmentBean = (UserRoleAssignmentBean) FaceContextUtils.getViewScopeBean("userRoleAssignmentBean");
			if (roleAssignmentBean != null) {
				roleAssignmentBean.reloadUserRoleList();
			}
		}
		
    }
}
