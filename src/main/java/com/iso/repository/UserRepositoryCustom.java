package com.iso.repository;

import java.util.List;

import com.iso.domain.Organization;
import com.iso.domain.User;

public interface UserRepositoryCustom {

	public List<User> searchLikeName(Organization organization, String name);
	
	public List<User> findUserAssignedToUserRole(String organizationId, String userRoleId);
	
	public List<User> findUserAssignedToOrgUnit(String organizationId, String orgUnitId);
	
}
