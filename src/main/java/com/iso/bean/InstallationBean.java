package com.iso.bean;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.ConfigurationException;
import org.primefaces.event.FlowEvent;
import org.springframework.dao.DataAccessException;

import com.iso.domain.ApplicationConfiguration;
import com.iso.domain.Category;
import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.domain.UserRole;
import com.iso.service.ApplicationConfigurationService;
import com.iso.service.impl.ApplicationConfigurationServiceImpl;
import com.iso.util.ConfigurationPropertiesUtils;
import com.iso.util.FaceContextUtils;
import com.iso.util.HashCode;

@ManagedBean(name="installationBean")
@ViewScoped
public class InstallationBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean multiOrganizationSetup;
	
	private Organization orgOwner;
	private User sysAdmin;
	private Category rootCat;
	private UserRole sysRole;
	
	@ManagedProperty(value="#{applicationConfigurationService}")
	private ApplicationConfigurationService appConfigService;
	
	@ManagedProperty("#{applicationSetupBean}")
    private ApplicationSetupBean appSetupBean;
	
	public ApplicationSetupBean getAppSetupBean() {
		return appSetupBean;
	}
	public void setAppSetupBean(ApplicationSetupBean appSetupBean) {
		this.appSetupBean = appSetupBean;
	}
	public boolean isMultiOrganizationSetup() {
		return multiOrganizationSetup;
	}
	public void setMultiOrganizationSetup(boolean multiOrganizationSetup) {
		this.multiOrganizationSetup = multiOrganizationSetup;
	}
	public Organization getOrgOwner() {
		return orgOwner;
	}
	public void setOrgOwner(Organization orgOwner) {
		this.orgOwner = orgOwner;
	}
	public User getSysAdmin() {
		return sysAdmin;
	}
	public void setSysAdmin(User sysAdmin) {
		this.sysAdmin = sysAdmin;
	}
	public UserRole getSysRole() {
		return sysRole;
	}
	public void setSysRole(UserRole sysRole) {
		this.sysRole = sysRole;
	}
	public Category getRootCat() {
		return rootCat;
	}
	public void setRootCat(Category rootCat) {
		this.rootCat = rootCat;
	}
	public ApplicationConfigurationService getAppConfigService() {
		return appConfigService;
	}
	public void setAppConfigService(ApplicationConfigurationService appConfigService) {
		this.appConfigService = appConfigService;
	}
	
	private void doCheckConfiguration() {
		try {
			boolean reinstall = Boolean.parseBoolean(ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.APP_CONFIGURATION_REINSTALL));
			ApplicationConfiguration appConfig = appConfigService.getLatestConfiguration();
			
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			HttpServletResponse response = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
			
			if(!reinstall && appConfig != null) {
				String installationUrI = request.getContextPath() + "/faces/login.xhtml";
				response.sendRedirect(installationUrI);
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when reading the configuration information", e.getStackTrace().toString());
		}
	}
	@PostConstruct
	private void intialize() {
		try {
			doCheckConfiguration();
			
			this.multiOrganizationSetup = Boolean.parseBoolean(ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.MULTI_ORGANIZATION_SETUP));
			
			this.orgOwner = new Organization();
			this.orgOwner.setCode(ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.ORGANIZATION_OWNER_CODE));
			this.orgOwner.setName(ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.ORGANIZATION_OWNER_NAME));
			
			this.sysAdmin = new User();
			this.sysAdmin.setUsername(ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.SYS_ADMIN_USERNAME));
			this.sysAdmin.setFullname(ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.SYS_ADMIN_FULLNAME));
			
			this.rootCat = new Category();
			this.rootCat.setName(ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.CATEGORY_ROOT_NAME));
			
			this.sysRole = new UserRole();
			this.sysRole.setCode(ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.SYS_ADMIN_ROLE_CODE));
			this.sysRole.setName(ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.SYS_ADMIN_ROLE_NAME));
			
		} catch (IOException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when loading configuration properties file", e.getStackTrace().toString());
		}
	}
	
	public String onFlowProcess(FlowEvent event) {
		return event.getNewStep();
    }
	
	public void doSave(ActionEvent event) {
		try {
			ApplicationConfiguration appConfig = new ApplicationConfiguration();
			appConfig.setOrgOwner(orgOwner);
			
			sysAdmin.setPassword(HashCode.getHashPassword(sysAdmin.getPassword()));
			appConfig.setSysAdmin(sysAdmin);
			appConfig.setSysUserRole(sysRole);
			
			appConfig.setMultiOrganizationSetup(this.multiOrganizationSetup);
			
			boolean isReInstallation = Boolean.parseBoolean(ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.APP_CONFIGURATION_REINSTALL));
			appConfig.setReInstallation(isReInstallation);
			
			appConfigService.save(appConfig);
			
			appSetupBean.setReinstall(false);
			appSetupBean.setMultiOrganizationSetup(multiOrganizationSetup);
			
			if (isReInstallation) {
				ConfigurationPropertiesUtils.setConfigurationProperties(ConfigurationPropertiesUtils.APP_CONFIGURATION_REINSTALL, Boolean.toString(false));
			}
			//ConfigurationPropertiesUtils.setConfigurationProperties(ConfigurationPropertiesUtils.MULTI_ORGANIZATION_SETUP, Boolean.toString(multiOrganizationSetup));
			
			Object bean = FaceContextUtils.getSessionScopeBean("authenticationBean");
			if (bean != null) {
				AuthenticationBean authBean = (AuthenticationBean) bean;
				authBean.reloadOrganizationList();
			}
			FaceContextUtils.redirect("/faces/installation/installationSuccess.xhtml");
			
		}catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when save default configuration", e.getStackTrace().toString());
		} catch (IOException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when save default configuration", e.getStackTrace().toString());
		} catch (ConfigurationException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when save default configuration", e.getStackTrace().toString());
		} 
	}
}
