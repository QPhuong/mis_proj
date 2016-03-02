package com.iso.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iso.domain.Organization;
import com.iso.domain.OrganizationUnit;
import com.iso.domain.User;
import com.iso.exception.BusinessException;

@Service
public interface OrganizationUnitService extends Serializable{

	public List<OrganizationUnit> getOrganizationChildByParent(Organization organization, OrganizationUnit parent);
	
	public OrganizationUnit getOrganizationRoot(Organization organization);
	
	public OrganizationUnit saveOrganizationUnitData(User loginUser, OrganizationUnit orgUnit) throws BusinessException;
	
	public OrganizationUnit saveUserAssignedToOrgUnit(User loginUser, OrganizationUnit orgUnit) throws BusinessException ;
	
	public void delete(User loginUser, OrganizationUnit orgUnit) throws BusinessException;
	
	/*public List<OrganizationUnit> getAllOrganizationUnit(Organization organization);*/
	
	public List<OrganizationUnit> searchByName(Organization organization, String name);
	
	public OrganizationUnit getOrganizationUnitById(String id);
	
}
