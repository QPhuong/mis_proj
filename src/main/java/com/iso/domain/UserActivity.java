package com.iso.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.iso.constant.LogTypes;
import com.iso.domain.base.BaseDomain;
import com.iso.domain.generic.DomainObjectVersion;

@Document
public class UserActivity extends BaseDomain{

	private static final long serialVersionUID = 1L;

	@Id
	private ObjectId id;
	@Field
	@Indexed
	private User user;
	@DBRef
	@Indexed
	private Object domainRef;
	@Field
	@Indexed
	private Date activityDate;
	@Field
	private String activiy;
	@Field
	private String version;
	@Field
	private String comment;
	@Field
	@Indexed
	private LogTypes logType;
	@DBRef 
	private UserSessionActivity userSession;
	@Field
	private DomainObjectVersion<?> domainVersion;	
	@Field
	private Map<String, Object> additionalAttributes;
	
	public UserActivity(){
		activityDate = Calendar.getInstance().getTime();
	}
	
	public UserActivity(User user, LogTypes logType) {
		this.user = user;
		this.logType = logType;
		activityDate = Calendar.getInstance().getTime();
	}
	
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
	public Date getActivityDate() {
		return activityDate;
	}
	public void setActivityDate(Date activityDate) {
		this.activityDate = activityDate;
	}
	public String getActiviy() {
		return activiy;
	}
	public void setActiviy(String activiy) {
		this.activiy = activiy;
	}
	public Map<String, Object> getAdditionalAttributes() {
		return additionalAttributes;
	}
	public void setAdditionalAttributes(Map<String, Object> additionalAttributes) {
		this.additionalAttributes = additionalAttributes;
	}
	public UserSessionActivity getUserSession() {
		return userSession;
	}
	public void setUserSession(UserSessionActivity userSession) {
		this.userSession = userSession;
	}
	public LogTypes getLogType() {
		return logType;
	}
	public void setLogType(LogTypes logType) {
		this.logType = logType;
	}
	public DomainObjectVersion<?> getDomainVersion() {
		return domainVersion;
	}
	public void setDomainVersion(DomainObjectVersion<?> domainVersion) {
		this.domainVersion = domainVersion;
	}

	public Object getDomainRef() {
		return domainRef;
	}

	public void setDomainRef(Object domainRef) {
		this.domainRef = domainRef;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
