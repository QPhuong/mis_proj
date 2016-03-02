package com.iso.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.iso.domain.base.BaseDomain;

@Document
public class OrganizationUnit extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private ObjectId id;
	@Field
	private String code;
	@Field
	private String name;
	@Field
	private String description;
	@DBRef
	private OrganizationUnit parent;
	@DBRef
	private Organization organization;
	@DBRef(lazy = true)
	private List<User> users;
	
	public OrganizationUnit(){};
	
	public OrganizationUnit(String code, String name, String description, Organization organization, OrganizationUnit parent){
		this.code = code;
		this.name = name;
		this.description = description;
		this.organization = organization;
		this.parent = parent;
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public OrganizationUnit getParent() {
		return parent;
	}
	public void setParent(OrganizationUnit parent) {
		this.parent = parent;
	}
	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	
}
