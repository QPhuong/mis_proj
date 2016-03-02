package com.iso.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.iso.domain.base.BaseDomain;
import com.iso.domain.generic.Metadata;
import com.iso.util.DomainSecurityChecker;
import com.iso.util.IsoFileSupportUtils;

@Document
public class IsoDocument extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private ObjectId id;
	@Field
	private ObjectId fileId;
	
	@Field
	@TextIndexed(weight=2)
	private String fileTitle;
	
	@Field
	@Indexed(background=true, sparse=true)
	private String extension;
	
	@Field
	@TextIndexed
	private String description;
	
	@Field
	private String contentType;
	
	@Field
	@Indexed(background=true, sparse=true)
	private long size;	
	
	@DBRef(lazy=true)
	@Indexed(background=true, sparse=true)
	private Organization organization;
	
	@DBRef(lazy=true)
	@Indexed(background=true, sparse=true)
	private Category category;
	
	@DBRef(lazy=true)
	@Indexed(background=true, sparse=true)
	private List<Category> additionalCategories;
	
	@Field
	@TextIndexed
	private List<Metadata<?>> properties;
	
	@Field
	private List<DocumentSecurity> documentSecurities;
	
	@Field
	@TextIndexed
	private List<DocumentNote> documentNotes;
	
	@Field
	@Indexed(background=true, sparse=true)
	private boolean template;
	@Field
	@Indexed(background=true, sparse=true)
	private boolean deleted;
	@Field
	@Indexed(background=true, sparse=true)
	private boolean locked;
	@DBRef
	@Indexed(background=true, sparse=true)
	private User lockedBy;
	@Field
	@Indexed(background=true, sparse=true)
	private Date lockedOn;
	@Field
	@Indexed(background=true, sparse=true)
	private boolean checkedOut;
	@Field
	private boolean published;
	@Field
	private Date publishedOn;
	@DBRef
	private User publishedBy;
	@Field
	private String latestVersion;
	
	@Transient
	private List<UserActivity> activities;
	@Transient
	private DocumentSecurity finalSecurity;
	
	public IsoDocument(){}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getFileId() {
		return fileId;
	}

	public void setFileId(ObjectId fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return this.fileTitle + "." + this.extension;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getSizeStr() {
		return IsoFileSupportUtils.humanReadableByteCount(this.size);
	}
	
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Metadata<?>> getProperties() {
		return properties;
	}

	public void setProperties(List<Metadata<?>> properties) {
		this.properties = properties;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Category> getAdditionalCategories() {
		return additionalCategories;
	}

	public void setAdditionalCategories(List<Category> additionalCategories) {
		this.additionalCategories = additionalCategories;
	}

	public boolean isTemplate() {
		return template;
	}

	public void setTemplate(boolean template) {
		this.template = template;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public boolean isCheckedOut() {
		return checkedOut;
	}

	public void setCheckedOut(boolean checkedOut) {
		this.checkedOut = checkedOut;
	}

	public User getLockedBy() {
		return lockedBy;
	}

	public void setLockedBy(User lockedBy) {
		this.lockedBy = lockedBy;
	}

	public Date getLockedOn() {
		return lockedOn;
	}

	public void setLockedOn(Date lockedOn) {
		this.lockedOn = lockedOn;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public Date getPublishedOn() {
		return publishedOn;
	}

	public void setPublishedOn(Date publishedOn) {
		this.publishedOn = publishedOn;
	}

	public User getPublishedBy() {
		return publishedBy;
	}

	public void setPublishedBy(User publishedBy) {
		this.publishedBy = publishedBy;
	}
	
	public String getLatestVersion() {
		return latestVersion;
	}

	public void setLatestVersion(String latestVersion) {
		this.latestVersion = latestVersion;
	}
	
	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getFileTitle() {
		return fileTitle;
	}

	public void setFileTitle(String fileTitle) {
		this.fileTitle = fileTitle;
	}

	public List<UserActivity> getActivities() {
		return activities;
	}

	public void setActivities(List<UserActivity> activities) {
		this.activities = activities;
	}

	public DocumentSecurity getFinalSecurity() {
		return finalSecurity;
	}

	public void setFinalSecurity(DocumentSecurity finalSecurity) {
		this.finalSecurity = finalSecurity;
	}

	public List<DocumentSecurity> getDocumentSecurities() {
		return documentSecurities;
	}

	public void setDocumentSecurities(List<DocumentSecurity> documentSecurity) {
		this.documentSecurities = documentSecurity;
	}

	public List<DocumentNote> getDocumentNotes() {
		return documentNotes;
	}

	public void setDocumentNotes(List<DocumentNote> documentNotes) {
		this.documentNotes = documentNotes;
	}

	public boolean canViewDocInfo(User user) {
		if (this.finalSecurity == null) {
			this.finalSecurity = DomainSecurityChecker.checkDocumentSecurity(user, this);
		}
		return this.finalSecurity.isCanViewDocInfo();
	}
	
	public boolean canDownload(User user) {
		if (this.finalSecurity == null) {
			this.finalSecurity = DomainSecurityChecker.checkDocumentSecurity(user, this);
		}
		return this.finalSecurity.isCanDownloadDoc();
	}
	
	public boolean canDelete(User user) {
		if (this.finalSecurity == null) {
			this.finalSecurity = DomainSecurityChecker.checkDocumentSecurity(user, this);
		}
		boolean result = this.finalSecurity.isCanDeleteDoc() && !this.locked;
		return result;
	}
	
	public boolean canEdit(User user) {
		if (this.finalSecurity == null) {
			this.finalSecurity = DomainSecurityChecker.checkDocumentSecurity(user, this);
		}
		boolean result = this.finalSecurity.isCanEditDoc() && !(this.locked || this.checkedOut || this.deleted);
		return result;
	}
	
	public boolean canLock(User user) {
		if (this.finalSecurity == null) {
			this.finalSecurity = DomainSecurityChecker.checkDocumentSecurity(user, this);
		}
		boolean result = this.finalSecurity.isCanEditDoc() && !(this.locked || this.checkedOut);
		return result;
	}
	
	public boolean canUnlock(User user) {
		if (this.finalSecurity == null) {
			this.finalSecurity = DomainSecurityChecker.checkDocumentSecurity(user, this);
		}
		if(this.finalSecurity.isCanEditDoc() && (this.locked || this.checkedOut)) {
			if (this.lockedBy != null && user != null && user.getId().equals(this.lockedBy.getId())) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public boolean canCheckOut(User user) {
		if (this.finalSecurity == null) {
			this.finalSecurity = DomainSecurityChecker.checkDocumentSecurity(user, this);
		}
		boolean result = this.finalSecurity.isCanEditDoc() && !(this.locked || this.checkedOut);
		return result;
	}
	
	public boolean canCheckIn(User user) {
		if (this.finalSecurity == null) {
			this.finalSecurity = DomainSecurityChecker.checkDocumentSecurity(user, this);
		}
		if(this.finalSecurity.isCanEditDoc() && this.checkedOut) {
			if (this.lockedBy != null && user != null && user.getId().equals(this.lockedBy.getId())) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	public IsoDocument clone() {
		
		IsoDocument cloned = new IsoDocument();
		cloned.additionalCategories = new ArrayList<Category>();
		cloned.additionalCategories.addAll(this.additionalCategories);
		cloned.category = this.category;
		cloned.contentType = this.contentType;
		cloned.description = this.description;
		cloned.fileId = this.fileId;
		cloned.fileTitle = this.fileTitle;
		cloned.extension = this.extension;
		cloned.latestVersion = this.latestVersion;
		cloned.properties = new ArrayList<Metadata<?>>();
		cloned.properties.addAll(this.properties);
		cloned.published = this.published;
		cloned.publishedBy = this.publishedBy;
		cloned.publishedOn = this.publishedOn;
		cloned.size = this.size;
		
		return cloned;
	}
	
	public boolean isMediaFile() {
		String extension = this.getExtension();
		List<String> mediaFileType = new ArrayList<String>();
		mediaFileType.add("aif");
		mediaFileType.add("iff");
		mediaFileType.add("m3u");
		mediaFileType.add("m4a");
		mediaFileType.add("mid");
		mediaFileType.add("mp3");
		mediaFileType.add("mpa");
		mediaFileType.add("ra");
		mediaFileType.add("wav");
		mediaFileType.add("wma");
		mediaFileType.add("3g2");
		mediaFileType.add("3gp");
		mediaFileType.add("asf");
		mediaFileType.add("asx");
		mediaFileType.add("avi");
		mediaFileType.add("flv");
		mediaFileType.add("m4v");
		mediaFileType.add("mov");
		mediaFileType.add("mp4");
		mediaFileType.add("mpg");
		mediaFileType.add("rm");
		mediaFileType.add("srt");
		mediaFileType.add("swf");
		mediaFileType.add("vob");
		mediaFileType.add("wmv");
		
		if(mediaFileType.contains(extension)) {
			return true;
		}
		return false;
	}
	
	public boolean isPDFFile() {
		String extension = this.getExtension();
		if("pdf".equalsIgnoreCase(extension)) {
			return true;
		}
		return false;
	}
	
	public boolean isImage() {
		String extension = this.getExtension();
		List<String> mediaFileType = new ArrayList<String>();
		mediaFileType.add("3dm");
		mediaFileType.add("3ds");
		mediaFileType.add("max");
		mediaFileType.add("obj");
		mediaFileType.add("bmp");
		mediaFileType.add("dds");
		mediaFileType.add("gif");
		mediaFileType.add("jpg");
		mediaFileType.add("png");
		mediaFileType.add("psd");
		mediaFileType.add("pspimage");
		mediaFileType.add("tga");
		mediaFileType.add("thm");
		mediaFileType.add("tif");
		mediaFileType.add("tiff");
		mediaFileType.add("yuv");
		mediaFileType.add("ai");
		mediaFileType.add("eps");
		mediaFileType.add("ps");
		mediaFileType.add("svg");
		
		if(mediaFileType.contains(extension)) {
			return true;
		}
		return false;
	}
	
}
