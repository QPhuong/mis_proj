package com.iso.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.exception.BusinessException;

@Service
public interface OrganizationService extends Serializable{

	public Organization saveOrganizationData(User loginUser, Organization org) throws BusinessException;
	
	public Organization saveOrganizationData(User loginUser, Organization org, String logDetail) throws BusinessException;
	
	public void delete(User loginUser,Organization org) throws BusinessException;
	
	public List<Organization> getAllOrganization();
	
	public List<Organization> getActiveOrganization();
	
	public List<Organization> searchOrganozationByName(String name);
	
	public Organization getOrganizationById(String id);
	
	public Organization getOrganizationByCode(String code);
}
