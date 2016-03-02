package com.iso.domain;

import java.io.Serializable;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.iso.domain.base.BaseDomain;

@Document
public class ApplicationConfiguration implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	private ObjectId id;
	@Field
	private Organization orgOwner;
	@Field
	private User sysAdmin;
	@Field
	private UserRole sysUserRole;
	@Field
	private boolean isMultiOrganizationSetup;
	@Field
	private boolean isInstallationCompleted;
	@Field
	private boolean isReInstallation;
	@Field
	private List<String> logs;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public Organization getOrgOwner() {
		return orgOwner;
	}
	public void setOrgOwner(Organization orgOwner) {
		this.orgOwner = orgOwner;
	}
	public User getSysAdmin() {
		return sysAdmin;
	}
	public void setSysAdmin(User sysAdmin) {
		this.sysAdmin = sysAdmin;
	}
	public UserRole getSysUserRole() {
		return sysUserRole;
	}
	public void setSysUserRole(UserRole sysUserRole) {
		this.sysUserRole = sysUserRole;
	}	
	public boolean isMultiOrganizationSetup() {
		return isMultiOrganizationSetup;
	}
	public void setMultiOrganizationSetup(boolean isMultiOrganizationSetup) {
		this.isMultiOrganizationSetup = isMultiOrganizationSetup;
	}
	public boolean isInstallationCompleted() {
		return isInstallationCompleted;
	}
	public void setInstallationCompleted(boolean isInstallationCompleted) {
		this.isInstallationCompleted = isInstallationCompleted;
	}
	public boolean isReInstallation() {
		return isReInstallation;
	}
	public void setReInstallation(boolean isReInstallation) {
		this.isReInstallation = isReInstallation;
	}
	public List<String> getLogs() {
		return logs;
	}
	public void setLogs(List<String> logs) {
		this.logs = logs;
	}
	
	
}
