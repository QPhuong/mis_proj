package com.iso.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.exception.BusinessException;
import com.iso.service.AuthenticationService;
import com.iso.service.OrganizationService;
import com.iso.service.UserService;
import com.iso.service.impl.OrganizationServiceImpl;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="authenticationBean")
@SessionScoped
public class AuthenticationBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userName;
	private String password;
	private User logindUser;
	private List<Organization> orgLst;
	private Organization selectedOrg;
	
	@ManagedProperty(value="#{userService}")
	private UserService userService;
	
	@ManagedProperty(value="#{organizationService}")
	private OrganizationService organizationService;
	
	@ManagedProperty(value="#{authenticationService}")
	private AuthenticationService authenticationService;
	
	@ManagedProperty("#{applicationSetupBean}")
    private ApplicationSetupBean appSetupBean;
	
	public ApplicationSetupBean getAppSetupBean() {
		return appSetupBean;
	}
	public void setAppSetupBean(ApplicationSetupBean appSetupBean) {
		this.appSetupBean = appSetupBean;
	}
	public List<Organization> getOrgLst() {
		return orgLst;
	}
	public void setOrgLst(List<Organization> orgLst) {
		this.orgLst = orgLst;
	}
	public Organization getSelectedOrg() {
		return selectedOrg;
	}
	public void setSelectedOrg(Organization selectedOrg) {
		this.selectedOrg = selectedOrg;
	}
	
	@PostConstruct
	private void initalize() {
		if (appSetupBean != null) {
			appSetupBean.doCheckApplicationConfiguration();
		}
		this.orgLst = organizationService.getActiveOrganization();
	}
	
	public void reloadOrganizationList() {
		this.orgLst = organizationService.getActiveOrganization();
	}
	
	public void reset(){
		userName = "";
		password = "";
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public OrganizationService getOrganizationService() {
		return organizationService;
	}
	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}
	
	public UserService getUserService() {
		return userService;
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public User getLogindUser() {
		return logindUser;
	}
	
	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}
	
	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	public void relogin() {
		if (this.logindUser != null && this.logindUser.getId() != null) {
			if (authenticationService.isActiveLoginSession()) {
				FaceContextUtils.redirect("/faces/document.xhtml");
			}
		}
	}
	
	public String login(){
		
		if (userName == null || password == null) {
			FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please input user name and password", "login.failed");
	        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
		    return ""; 
		}
		
		this.logindUser = authenticationService.login(userName, password, this.selectedOrg);
		
		if(this.logindUser != null && this.logindUser.getId() != null) {
			reset();
			return "success"; 
		}
				
		FaceContextUtils.showErrorMessage("User name and password are not correct", "login.failed");
	    return "";
	}

	public void logout() {
		authenticationService.logout(logindUser);
	}
	
	public void logoutAndRedirect() {
		logout();
		FaceContextUtils.redirect("/faces/login.xhtml");
	}
	
	public void doSaveUserProfile() {
		try {
			this.logindUser = userService.saveUserProfile(logindUser, logindUser);
			
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('userProfileDialogVar').hide();");
			
		} catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
		}
	}
	
	public void doCancelUserProfile() {
		
		this.logindUser = userService.getUserById(this.logindUser.getId().toString());
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('userProfileDialogVar').hide();");
	}

}
