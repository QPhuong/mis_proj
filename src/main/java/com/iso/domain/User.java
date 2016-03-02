package com.iso.domain;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.iso.domain.base.BaseDomain;

@Document
public class User extends BaseDomain{

	private static final long serialVersionUID = 1L;
	
	@Id
	private ObjectId id;
	@Field
	private String username;
	@Field
	private String fullname;
	@Transient
	private String searchDisplayedName;
	@Field
	private String password;
	@Field
	private String email;
	@DBRef
	private Organization organization;
	@DBRef
	private List<UserRole> userRoles;
	@DBRef
	private List<OrganizationUnit> organizationUnits;
	@Field
	@Indexed(background=true, sparse=true)
	private boolean active;
	@Field
	private boolean locked;
	
	public User() {
		active = true;
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getSearchDisplayedName() {
		if (StringUtils.isEmpty(this.searchDisplayedName)) {
			this.searchDisplayedName = this.fullname + " (" + this.username + ")";
		}
		return searchDisplayedName;
	}
	public void setSearchDisplayedName(String searchDisplayedName) {
		this.searchDisplayedName = searchDisplayedName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	public List<UserRole> getUserRoles() {
		return userRoles;
	}
	public void setUserRoles(List<UserRole> userRoles) {
		this.userRoles = userRoles;
	}
	public List<OrganizationUnit> getOrganizationUnits() {
		return organizationUnits;
	}
	public void setOrganizationUnits(List<OrganizationUnit> organizationUnits) {
		this.organizationUnits = organizationUnits;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isOrganizationAdministrator() {
		if (CollectionUtils.isNotEmpty(this.userRoles)) {
			for (UserRole role : this.userRoles) {
				if (role.isOrganizationAdminRole()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isSysAdministrator() {
		if (CollectionUtils.isNotEmpty(this.userRoles)) {
			for (UserRole role : this.userRoles) {
				if (role.isSystemAdminRole()) {
					return true;
				}
			}
		}
		return false;
	}
}
