package com.iso.constant;

import java.io.Serializable;
import java.util.Date;

import com.iso.domain.Category;
import com.iso.domain.Organization;
import com.iso.domain.User;


public class DocumentSearchCriteria implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public enum DocumentSearchType {
		CHANGED("Documents have been changed"), 
		LOCKED("Documents have been locked"), 
		NEW("Documents have been created"),
		DELETE("Documents have been deleted");
		
		private String text;
		
		private DocumentSearchType(String text) {
			this.text = text;
		}
		
		public String toString(){
		       return text;
	    }
	}
	
	private String searchText;
	private DocumentSearchType type;
	
	private Date from;
	private Date to;
	private User user;
	private int limit;
	private long sizeFrom;
	private long sizeTo;
	private Category category;
	private Organization organization;
	
	public String getSearchText() {
		return searchText;
	}
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	public DocumentSearchType getType() {
		return type;
	}
	public void setType(DocumentSearchType type) {
		this.type = type;
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
	public long getSizeFrom() {
		return sizeFrom;
	}
	public void setSizeFrom(long sizeFrom) {
		this.sizeFrom = sizeFrom;
	}
	public long getSizeTo() {
		return sizeTo;
	}
	public void setSizeTo(long sizeTo) {
		this.sizeTo = sizeTo;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
}
