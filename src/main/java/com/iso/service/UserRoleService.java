package com.iso.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.domain.UserRole;
import com.iso.exception.BusinessException;

@Service
public interface UserRoleService extends Serializable{

	public UserRole saveUserRoleData(User loginUser, UserRole userRole) throws BusinessException;
	
	public UserRole saveMenuSecurityAssignedToRole(User loginUser, UserRole userRole) throws BusinessException;
	
	public void delete(User loginUser, UserRole userRole) throws BusinessException;
	
	public List<UserRole> getUserRoleByOrganization(Organization organization);
	
	public UserRole getUserRoleById(String id);
	
	public UserRole getUserRoleByCodeAndOrganization(String code, Organization organization);
}
