package com.iso.repository;

import java.util.List;

import com.iso.domain.OrganizationUnit;

public interface OrganizationUnitRepositoryCustom {

	public List<OrganizationUnit> findByAssignedUser(String userId);
}
