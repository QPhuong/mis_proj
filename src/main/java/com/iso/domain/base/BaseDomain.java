package com.iso.domain.base;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.iso.domain.User;
import com.iso.util.DateTimeUtils;

@Document
public class BaseDomain implements Serializable{

	private static final long serialVersionUID = 1L;
	@DBRef
	@Indexed(background=true, sparse=true)
	private User createdBy;
	@Field
	@Indexed(background=true, sparse=true)
	private Date createdOn;
	@DBRef
	@Indexed(background=true, sparse=true)
	private User updatedBy;
	@Field
	@Indexed(background=true, sparse=true)
	private Date updatedOn;
	@Field
	@Indexed(background=true, sparse=true)
	private Date deletedOn;
	@DBRef
	@Indexed(background=true, sparse=true)
	private User deletedBy;
	
	public User getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public String getCreatedOnStr() {
		return DateTimeUtils.formatDateTime(createdOn);
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public User getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Date getUpdatedOn() {
		return updatedOn;
	}
	public String getUpdatedOnStr() {
		return DateTimeUtils.formatDateTime(updatedOn);
	}
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
	public Date getDeletedOn() {
		return deletedOn;
	}
	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}
	public User getDeletedBy() {
		return deletedBy;
	}
	public void setDeletedBy(User deletedBy) {
		this.deletedBy = deletedBy;
	}
	
}
