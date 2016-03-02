package com.iso.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class UserSessionActivity implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	private ObjectId id;
	@Field
	private User user;
	@Field
	private String ipAddress;
	@Field
	private String sessionId;
	@Field
	private String activity;
	@Field
	private String activityDetail;
	@Field
	private Date activityDate;
	@Field
	private Map<String, Object> attributes;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public Date getActivityDate() {
		return activityDate;
	}
	public void setActivityDate(Date activityDate) {
		this.activityDate = activityDate;
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	public String getActivityDetail() {
		return activityDetail;
	}
	public void setActivityDetail(String activityDetail) {
		this.activityDetail = activityDetail;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
