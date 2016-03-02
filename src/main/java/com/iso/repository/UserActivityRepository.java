package com.iso.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.iso.domain.UserActivity;

public interface UserActivityRepository extends PagingAndSortingRepository<UserActivity, String>, UserActivityRepositoryCustom{

	public List<UserActivity> findByDomainRefOrderByActivityDateDesc(Object domainRef);
	
}
