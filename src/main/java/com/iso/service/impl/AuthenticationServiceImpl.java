package com.iso.service.impl;

import java.util.HashSet;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.stereotype.Component;

import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.domain.UserRole;
import com.iso.repository.UserRepository;
import com.iso.service.AuthenticationService;
import com.iso.service.UserSessionActivityService;

@Component(value="authenticationService")
public class AuthenticationServiceImpl implements AuthenticationService{

	private static final long serialVersionUID = 1L;

	@Autowired
	private AuthenticationManager authenticationManager;	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	@Qualifier(value="userSessionActivityService")
	private UserSessionActivityService userSessionActivityService;
	
	private Organization selectedOrg;
	
	public Organization getSelectedOrg() {
		return selectedOrg;
	}

	public User login(String username, String password, Organization organization) {
		try {
			User loginUser;
			if (organization != null && organization.getId() != null) {
				this.selectedOrg = organization;
				loginUser = userRepository.findByUsernameAndOrganizationAndActiveIsTrue(username, organization);
			} else {
				loginUser = userRepository.findByUsernameAndActiveIsTrue(username);
			}
			
			if (loginUser != null && loginUser.getId() != null) {
		        
		        Set<GrantedAuthority> lstAuthority = new HashSet<GrantedAuthority>();
		        if(CollectionUtils.isNotEmpty(loginUser.getUserRoles())){
		        	for(UserRole role : loginUser.getUserRoles()) {
		        		lstAuthority.add(new SimpleGrantedAuthority(role.getCode()));
		        	}
		        }
		        
		        // authenticate against spring security
				Authentication request = new UsernamePasswordAuthenticationToken(username,password, lstAuthority);            
		        Authentication result = authenticationManager.authenticate(request);
		        
		        SecurityContextHolder.getContext().setAuthentication(result);
		        if (result.isAuthenticated()) {
		        	userSessionActivityService.saveLoginActivityLog(loginUser);
		        	return loginUser;
		        }
			}
			return null;
		} catch (AuthenticationException e) {
			FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Authentication error", "login.failed");
	        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
		    return null;
		}
	}
	
	public boolean logout(User loginUser) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		 if (auth != null){ 
			 userSessionActivityService.saveLogoutActivityLog(loginUser);
			 
			 HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
			 HttpServletResponse response = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
			 new SecurityContextLogoutHandler().logout(request, response, auth);
			 new PersistentTokenBasedRememberMeServices().logout(request, response, auth);
			 auth.setAuthenticated(false);
		 }
		return true;
	}
	
	public boolean isActiveLoginSession() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		 if (auth != null && auth.isAuthenticated()){
			 if(auth.getAuthorities().size() == 1) {
				 GrantedAuthority authority = auth.getAuthorities().iterator().next();
				 if(authority.getAuthority().equals("ROLE_ANONYMOUS")) {
					 return false;
				 }
			 }
		 }
		 return auth.isAuthenticated();
	}
}
