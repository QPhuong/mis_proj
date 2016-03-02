package com.iso.domain;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.iso.domain.base.BaseDomain;

@Document
public class DocumentVersion extends BaseDomain{

	private static final long serialVersionUID = 1L;

	@Id
	private ObjectId id;
	@Field
	private IsoDocument isoDocument;
	@Field
	private String version;
	@Field
	private Date versionDate;
	@Field
	private boolean majorVersion;
	@Field
	private String comment;
	@DBRef
	private User user;
	
	public DocumentVersion(){}
	
	public DocumentVersion(User user, IsoDocument isoDocument) {
		this.user = user;
		this.isoDocument = isoDocument;
		this.version = isoDocument.getLatestVersion();
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public IsoDocument getIsoDocument() {
		return isoDocument;
	}
	public void getIsoDocument(IsoDocument isoDocument) {
		this.isoDocument = isoDocument;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public boolean isMajorVersion() {
		return majorVersion;
	}
	public void setMajorVersion(boolean majorVersion) {
		this.majorVersion = majorVersion;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Date getVersionDate() {
		return versionDate;
	}
	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}
}
