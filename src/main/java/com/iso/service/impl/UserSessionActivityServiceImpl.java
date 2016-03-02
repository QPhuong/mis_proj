package com.iso.service.impl;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iso.constant.UserSessionActivitiesLogs;
import com.iso.domain.User;
import com.iso.domain.UserSessionActivity;
import com.iso.repository.UserSessionActivityRepository;
import com.iso.service.UserSessionActivityService;

@Component(value="userSessionActivityService")
public class UserSessionActivityServiceImpl implements UserSessionActivityService{

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private UserSessionActivityRepository userSessionActivityRepo;
	@Autowired
    private HttpServletRequest request;
	
	public UserSessionActivity saveLoginActivityLog(User user) {
		
		UserSessionActivity activity = new UserSessionActivity();
		activity.setIpAddress(request.getRemoteAddr());
		activity.setSessionId(request.getSession().getId());
		activity.setActivity(UserSessionActivitiesLogs.LOGIN_ACTIVITY);
		activity.setActivityDate(Calendar.getInstance().getTime());
		activity.setActivityDetail(UserSessionActivitiesLogs.LOGIN_ACTIVITY_DETAIL);
		activity.setUser(user);
		
		activity = userSessionActivityRepo.save(activity);
		
		return activity;
	}
	
	public UserSessionActivity saveLogoutActivityLog(User user) {
		
		UserSessionActivity activity = new UserSessionActivity();
		activity.setIpAddress(request.getRemoteAddr());
		activity.setSessionId(request.getSession().getId());
		activity.setActivity(UserSessionActivitiesLogs.LOGOUT_ACTIVITY);
		activity.setActivityDate(Calendar.getInstance().getTime());
		activity.setActivityDetail(UserSessionActivitiesLogs.LOGOUT_ACTIVITY_DETAIL);
		activity.setUser(user);
		
		activity = userSessionActivityRepo.save(activity);
		
		return activity;
	}
	
	/*public UserSessionActivity getCurrentUserSessionActitivy(User user) {
		return userSessionActivityRepo.findByUserAndIpAddressAndSessionId(user, request.getRemoteAddr(), request.getSession().getId());
	}*/
	
	public UserSessionActivity getCurrentUserSessionActitivy(String userId) {
		return userSessionActivityRepo.findByUserIdAndIPAddressAndSessionId(userId, request.getRemoteAddr(), request.getSession().getId());
	}
}
