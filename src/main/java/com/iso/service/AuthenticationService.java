package com.iso.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import com.iso.domain.Organization;
import com.iso.domain.User;

@Service
public interface AuthenticationService extends Serializable{

	public Organization getSelectedOrg();
	
	public User login(String username, String password, Organization organization);
	
	public boolean logout(User loginUser);
	
	public boolean isActiveLoginSession();
	
}
