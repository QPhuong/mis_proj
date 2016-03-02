package com.iso.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.iso.domain.Organization;
import com.iso.domain.OrganizationUnit;
import com.iso.domain.User;

public interface UserRepository extends PagingAndSortingRepository<User, String>, UserRepositoryCustom{

	public User findByUsername(String username); 
	public User findByUsernameAndActiveIsTrue(String username);
	
	public User findByUsernameAndOrganization(String username, Organization organization);
	public User findByUsernameAndOrganizationAndActiveIsTrue(String username, Organization organization);
	
	public List<User> findByOrganizationOrderByIdAsc(Organization organization);
	public List<User> findByOrganizationAndOrganizationUnitsOrderByIdAsc(Organization organization, OrganizationUnit orgUnit);
	
}
