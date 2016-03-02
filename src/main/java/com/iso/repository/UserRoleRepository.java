package com.iso.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.iso.domain.Organization;
import com.iso.domain.UserRole;

public interface UserRoleRepository extends PagingAndSortingRepository<UserRole, String> {

	public UserRole findByCode(String code);
	public UserRole findByCodeAndOrganization(String code, Organization organization);
	public List<UserRole> findByIsSystemAdminRoleOrderByNameAsc(boolean isSystemRole);
	public List<UserRole> findByOrganization(Organization organization);
}
