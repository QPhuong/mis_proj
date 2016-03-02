package com.iso.constant;

import java.io.Serializable;
import java.util.Date;

import com.iso.domain.User;

public class UserActivityCriteria implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private LogTypes logType;
	private Date from;
	private Date to;
	private User user;
	private int limit;
	
	public LogTypes getLogType() {
		return logType;
	}
	public void setLogType(LogTypes logType) {
		this.logType = logType;
	}
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
}
