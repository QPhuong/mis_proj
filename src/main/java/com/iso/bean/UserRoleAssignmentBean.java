package com.iso.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.model.DualListModel;
import org.springframework.dao.DataAccessException;

import com.iso.domain.User;
import com.iso.domain.UserRole;
import com.iso.exception.BusinessException;
import com.iso.service.UserRoleService;
import com.iso.service.UserService;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="userRoleAssignmentBean")
@ViewScoped
public class UserRoleAssignmentBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value="#{userService}")
	private UserService userService;
	
	@ManagedProperty(value="#{userRoleService}")
	private UserRoleService userRoleService;
	
	private User selectedUser;
	
	private DualListModel<UserRole> userRoleList;
	
	@PostConstruct
	public void initialize() {
		try{
			reloadUserRoleList();
		}catch(DataAccessException e) {
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error when search user role!",null));
		}
	}

	public User getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserRoleService getUserRoleService() {
		return userRoleService;
	}

	public void setUserRoleService(UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}

	public DualListModel<UserRole> getUserRoleList() {
		return userRoleList;
	}

	public void setUserRoleList(DualListModel<UserRole> userRoleList) {
		this.userRoleList = userRoleList;
	}

	public void reloadUserRoleList() {
		UserBean userBean = (UserBean) FaceContextUtils.getViewScopeBean("userBean");
		this.selectedUser = userBean.getSelectedUser();
		
		List<UserRole> allRoles = userRoleService.getUserRoleByOrganization(this.selectedUser.getOrganization());
		List<UserRole> assignedRoles = CollectionUtils.isEmpty(this.selectedUser.getUserRoles()) ? new ArrayList<UserRole>() : this.selectedUser.getUserRoles();
		List<UserRole> sourceRoles = filterUserRoleSource(allRoles, assignedRoles);
		
		userRoleList = new DualListModel<UserRole>(sourceRoles, assignedRoles);
	}
	
	private List<UserRole> filterUserRoleSource(List<UserRole> lstAllUserRole, List<UserRole> targetLst) {
		List<UserRole> sourceLst = new ArrayList<UserRole>();
		sourceLst.addAll(lstAllUserRole);
		
		if (!CollectionUtils.isEmpty(lstAllUserRole)) {
			if (CollectionUtils.isEmpty(targetLst)) {
				return sourceLst;
			} else {
				for(UserRole role : lstAllUserRole) {
					for (UserRole assignedRole : targetLst) {
						if (role.getCode().equalsIgnoreCase(assignedRole.getCode())) {
							sourceLst.remove(role);
						}
					}
				}
			}
		}
		return sourceLst;
	}
	
	public void doSaveUserRoleAssignment(ActionEvent event) {
		try{
			this.selectedUser.setUserRoles(this.userRoleList.getTarget());
			userService.saveUserRoleAssignment(this.selectedUser, FaceContextUtils.getLoginUser());
			
			FaceContextUtils.showInfoMessage("Save Successfully!", null);
		}catch(DataAccessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when save assigned roles!", null);
		} catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), null);
		}
	}
	
	public void doReset(ActionEvent event) {
		try{
			reloadUserRoleList();
		}catch(DataAccessException e) {
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error when search user!",null));
		}
	}
}
