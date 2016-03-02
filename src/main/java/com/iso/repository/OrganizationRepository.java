package com.iso.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.iso.domain.Organization;

public interface OrganizationRepository extends PagingAndSortingRepository<Organization, String>, OrganizationRepositoryCustom {

	public Organization findByCode(String code);
	
	public List<Organization> findByNameLikeIgnoreCaseOrderByNameAsc(String name);
	
	public List<Organization> findByActiveOrderByNameAsc(Boolean active);
}
