package com.iso.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iso.constant.UserActivityCriteria;
import com.iso.domain.UserActivity;

@Service
public interface UserActivityService extends Serializable{
	
	public void save(UserActivity activity);
	
	public List<UserActivity> getDomainHistories(Object domain);
	
	public List<UserActivity> searchActivites(UserActivityCriteria criteria);

}
