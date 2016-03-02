package com.iso.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.iso.domain.Organization;
import com.iso.domain.OrganizationUnit;

public interface OrganizationUnitRepository extends PagingAndSortingRepository<OrganizationUnit, String>, OrganizationUnitRepositoryCustom {

	public OrganizationUnit findByCode(String code);
	
	public OrganizationUnit findByCodeAndOrganization(String code, Organization organization);
	
	public List<OrganizationUnit> findByOrganizationAndNameLikeIgnoreCaseOrderByNameAsc(Organization organization, String name);
	
	public List<OrganizationUnit> findByOrganizationAndParentOrderByNameAsc(Organization organization, OrganizationUnit parent);
	
	public List<OrganizationUnit> findByParentOrderByIdAsc(OrganizationUnit parent);
	
	public OrganizationUnit findByOrganizationOrderByIdAsc(Organization organization);
}
