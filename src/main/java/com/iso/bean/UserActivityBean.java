package com.iso.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.iso.constant.LogTypes;
import com.iso.constant.UserActivityCriteria;
import com.iso.domain.User;
import com.iso.domain.UserActivity;
import com.iso.service.UserActivityService;
import com.iso.service.UserService;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="userActivityBean")
@ViewScoped
public class UserActivityBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private List<UserActivity> activities;
	private UserActivityCriteria criteria;
	
	private List<SelectItem> logTypes;
	
	@ManagedProperty(value="#{userActivityService}")
	private UserActivityService userActivityService;
	
	@ManagedProperty(value="#{userService}")
	private UserService userService;
	
	public List<UserActivity> getActivities() {
		return activities;
	}

	public void setActivities(List<UserActivity> activities) {
		this.activities = activities;
	}

	public UserActivityCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(UserActivityCriteria criteria) {
		this.criteria = criteria;
	}

	public List<SelectItem> getLogTypes() {
		return logTypes;
	}

	public void setLogTypes(List<SelectItem> logTypes) {
		this.logTypes = logTypes;
	}

	public UserActivityService getUserActivityService() {
		return userActivityService;
	}

	public void setUserActivityService(UserActivityService userActivityService) {
		this.userActivityService = userActivityService;
	}
	
	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@PostConstruct
	private void initialize() {
		this.activities = new ArrayList<UserActivity>();
		this.criteria = new UserActivityCriteria();
		
		this.logTypes = new ArrayList<SelectItem>();
		for (LogTypes type : LogTypes.values()) {
			this.logTypes.add(new SelectItem(type, type.toString()));
		}
	}
	
	public List<User> doSearchUser(String searchText) {
		return userService.searchByName(FaceContextUtils.getLoginUser().getOrganization(), searchText);
	}
	
	public void doSearch() {
		this.activities = userActivityService.searchActivites(this.criteria);
	}
	
	public void doReset() {
		this.criteria.setFrom(null);
		this.criteria.setTo(null);
		this.criteria.setLogType(null);
		this.criteria.setUser(null);
	}
	
	public void doExport() {
		
	}
	
}
