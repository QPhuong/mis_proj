package com.iso.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import com.iso.domain.User;
import com.iso.domain.UserSessionActivity;

@Service
public interface UserSessionActivityService extends Serializable{
	
	public UserSessionActivity saveLoginActivityLog(User user);
	
	public UserSessionActivity saveLogoutActivityLog(User user);
	
	public UserSessionActivity getCurrentUserSessionActitivy(String userId);
	
}
