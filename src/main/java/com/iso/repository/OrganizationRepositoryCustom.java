package com.iso.repository;

import java.util.List;

import com.iso.domain.Organization;

public interface OrganizationRepositoryCustom {

	public List<Organization> searchLikeName(String name); 
	
}
