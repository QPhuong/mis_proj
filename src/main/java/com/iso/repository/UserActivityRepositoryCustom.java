package com.iso.repository;

import java.util.List;

import com.iso.constant.UserActivityCriteria;
import com.iso.domain.UserActivity;

public interface UserActivityRepositoryCustom {

	public List<UserActivity> searchByCriteria(UserActivityCriteria criteria);
	
}
