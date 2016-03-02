package com.iso.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iso.domain.Organization;
import com.iso.domain.OrganizationUnit;
import com.iso.domain.User;
import com.iso.exception.BusinessException;


@Service
public interface UserService extends Serializable{

	public User saveUserProfile(User user, User loginUser) throws BusinessException;
	
	public User saveOrgUnitAssignment(User user, User loginUser) throws BusinessException;
	
	public User saveUserRoleAssignment(User user, User loginUser) throws BusinessException;
	
	public void delete(String userId, User loginUser) throws BusinessException;
	
	public User getUserById(String id);
	
	public User getUserByUserName(String username);
	
	public User getUserByUserNameAndOrganization(String username, Organization organization);
	
	public List<User> getUserLstByOrganization(Organization organization);
	
	public List<User> getUserLstByOrganizationAndOrganizationUnit(Organization org, OrganizationUnit orgUnit);
	
	public List<User> searchByName(Organization organization, String name);
	
}
