package com.iso.bean;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iso.domain.ApplicationConfiguration;
import com.iso.service.ApplicationConfigurationService;
import com.iso.util.ConfigurationPropertiesUtils;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="applicationSetupBean")
@ApplicationScoped
public class ApplicationSetupBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private boolean reinstall;
	private boolean isDemoMode;
	private boolean isMultiOrganizationSetup;
	
	public boolean isMultiOrganizationSetup() {
		return isMultiOrganizationSetup;
	}
	public void setMultiOrganizationSetup(boolean isMultiOrganizationSetup) {
		this.isMultiOrganizationSetup = isMultiOrganizationSetup;
	}
	public boolean isReinstall() {
		return reinstall;
	}
	public void setReinstall(boolean reinstall) {
		this.reinstall = reinstall;
	}
	public boolean isDemoMode() {
		return isDemoMode;
	}
	public void setDemoMode(boolean isDemoMode) {
		this.isDemoMode = isDemoMode;
	}

	@ManagedProperty(value="#{applicationConfigurationService}")
	private ApplicationConfigurationService appConfigService;
	
	public ApplicationConfigurationService getAppConfigService() {
		return appConfigService;
	}
	public void setAppConfigService(ApplicationConfigurationService appConfigService) {
		this.appConfigService = appConfigService;
	}
	
	
	@PostConstruct
    private void initialize() {
		try {
			isDemoMode = Boolean.parseBoolean(ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.APP_DEMO_MODE));
			reinstall = Boolean.parseBoolean(ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.APP_CONFIGURATION_REINSTALL));
			isMultiOrganizationSetup = Boolean.parseBoolean(ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.MULTI_ORGANIZATION_SETUP));
			
			if(appConfigService != null) {
				ApplicationConfiguration appConfig = appConfigService.getLatestConfiguration();
				if(appConfig != null) {
					this.isMultiOrganizationSetup = appConfig.isMultiOrganizationSetup();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when reading the configuration information", e.getStackTrace().toString());
		}
		
    }
	
	public void doCheckApplicationConfiguration() {
		ApplicationConfiguration appConfig = appConfigService.getLatestConfiguration();
		
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpServletResponse response = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
		
		if(reinstall || appConfig == null) {
			String installationUrI = request.getContextPath() + "/faces/installation/installation.xhtml";
			try {
				response.sendRedirect(installationUrI);
			} catch (IOException e) {
				e.printStackTrace();
				FaceContextUtils.showErrorMessage("Error when reading the configuration information", e.getStackTrace().toString());
			}
		}
		
	}

	
	
}
