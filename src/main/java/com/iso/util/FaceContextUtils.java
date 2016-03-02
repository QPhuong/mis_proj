package com.iso.util;

import java.io.IOException;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.iso.bean.AuthenticationBean;
import com.iso.domain.User;

public class FaceContextUtils {

	public static Object getViewScopeBean(String beanName) {
		Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap(); 
		return viewMap.get(beanName);
	}
	
	public static Object getSessionScopeBean(String beanName) {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		return sessionMap.get(beanName);
	}
	
	public static Object getApplicationScopeBean(String beanName) {
		Map<String, Object> applicationMap = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
		return applicationMap.get(beanName);
	}
	
	public static User getLoginUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			UserDetails userDetails = (UserDetails) auth.getPrincipal();
			AuthenticationBean authenticationBean = (AuthenticationBean) FaceContextUtils.getSessionScopeBean("authenticationBean");
			return authenticationBean.getLogindUser();
		} 
		return null;
	}
	
	public static void showErrorMessage(String clientId, String summary, String detail) {
		FacesContext context = FacesContext.getCurrentInstance();  
		if (context != null)
			context.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
	}
	
	public static void showErrorMessage(String summary, String detail) {
		FacesContext context = FacesContext.getCurrentInstance();  
		if (context != null)
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
	}
	
	public static void showInfoMessage(String clientId, String summary, String detail) {
		FacesContext context = FacesContext.getCurrentInstance();  
		if (context != null)	
			context.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
	}
	
	public static void showInfoMessage(String summary, String detail) {
		FacesContext context = FacesContext.getCurrentInstance();  
		if (context != null)
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
	}
	
	public static void showWarningMessage(String clientId, String summary, String detail) {
		FacesContext context = FacesContext.getCurrentInstance();  
		if (context != null)
			context.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail));
	}
	
	public static void showWarningMessage(String summary, String detail) {
		FacesContext context = FacesContext.getCurrentInstance();  
		if (context != null)
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail));
	}
	
	public static void redirect(String uri) {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		try {
			context.redirect(request.getContextPath() + uri);
		} catch (IOException e) {
			e.printStackTrace();
			showErrorMessage("Error when redirect page !", e.getStackTrace().toString());
		}
	}
}
