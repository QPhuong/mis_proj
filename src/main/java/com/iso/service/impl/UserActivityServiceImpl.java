package com.iso.service.impl;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.iso.constant.UserActivityCriteria;
import com.iso.domain.User;
import com.iso.domain.UserActivity;
import com.iso.domain.UserSessionActivity;
import com.iso.repository.UserActivityRepository;
import com.iso.repository.UserRepository;
import com.iso.service.UserActivityService;
import com.iso.service.UserSessionActivityService;

@Component(value="userActivityService")
public class UserActivityServiceImpl implements UserActivityService{

	private static final long serialVersionUID = 1L;

	@Autowired
	private UserActivityRepository userActivityRepo;
	
	@Autowired
	@Qualifier(value="userSessionActivityService")
	private UserSessionActivityService userSessionActivityService;
	
	@Autowired
	private UserRepository userRepository;
	
	public void save(UserActivity activity){
		
		UserSessionActivity userSession = userSessionActivityService.getCurrentUserSessionActitivy(activity.getUser().getId().toString());
		
		activity.setActivityDate(Calendar.getInstance().getTime());
		activity.setUserSession(userSession);
		
		userActivityRepo.save(activity);
		
		if (activity.getUser().isActive() && !activity.getUser().isLocked()) {
			User user = userRepository.findOne(activity.getUser().getId().toString());
			user.setLocked(true);
			userRepository.save(user);
			
		}
	}
	
	public List<UserActivity> getDomainHistories(Object domain) {
		return userActivityRepo.findByDomainRefOrderByActivityDateDesc(domain);
	}
	
	public List<UserActivity> searchActivites(UserActivityCriteria criteria) {
		return userActivityRepo.searchByCriteria(criteria);
	}
	
}
