package com.iso.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.iso.domain.UserSessionActivity;

public interface UserSessionActivityRepository extends PagingAndSortingRepository<UserSessionActivity, String>, UserSessionActivityRepositoryCustom{

}
