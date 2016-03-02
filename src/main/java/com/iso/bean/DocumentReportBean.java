package com.iso.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.ToggleEvent;

import com.iso.constant.DocumentSearchCriteria;
import com.iso.domain.IsoDocument;
import com.iso.domain.User;
import com.iso.domain.UserActivity;
import com.iso.service.DocumentVersionService;
import com.iso.service.IsoDocumentService;
import com.iso.service.UserActivityService;
import com.iso.service.UserService;
import com.iso.util.FaceContextUtils;

@ManagedBean
@ViewScoped
public class DocumentReportBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private List<IsoDocument> lstDocument;
	private List<UserActivity> activities;
	private DocumentSearchCriteria criteria;
	
	private List<SelectItem> types;
	
	@ManagedProperty(value="#{userActivityService}")
	private UserActivityService userActivityService;
	
	@ManagedProperty(value="#{userService}")
	private UserService userService;
	
	@ManagedProperty(value="#{isoDocumentService}")
	private IsoDocumentService isoDocumentService;
	
	@ManagedProperty(value="#{documentVersionService}")
	private DocumentVersionService documentVersionService;

	
	public List<IsoDocument> getLstDocument() {
		return lstDocument;
	}

	public void setLstDocument(List<IsoDocument> lstDocument) {
		this.lstDocument = lstDocument;
	}

	public List<UserActivity> getActivities() {
		return activities;
	}

	public void setActivities(List<UserActivity> activities) {
		this.activities = activities;
	}

	public DocumentSearchCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(DocumentSearchCriteria criteria) {
		this.criteria = criteria;
	}

	public List<SelectItem> getTypes() {
		return types;
	}

	public void setTypes(List<SelectItem> types) {
		this.types = types;
	}

	public UserActivityService getUserActivityService() {
		return userActivityService;
	}

	public void setUserActivityService(UserActivityService userActivityService) {
		this.userActivityService = userActivityService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public IsoDocumentService getIsoDocumentService() {
		return isoDocumentService;
	}

	public void setIsoDocumentService(IsoDocumentService isoDocumentService) {
		this.isoDocumentService = isoDocumentService;
	}

	public DocumentVersionService getDocumentVersionService() {
		return documentVersionService;
	}

	public void setDocumentVersionService(DocumentVersionService documentVersionService) {
		this.documentVersionService = documentVersionService;
	}
	
	@PostConstruct
	private void initialize() {
		this.activities = new ArrayList<UserActivity>();
		this.criteria = new DocumentSearchCriteria();
		
		this.types = new ArrayList<SelectItem>();
		for (DocumentSearchCriteria.DocumentSearchType type : DocumentSearchCriteria.DocumentSearchType.values()) {
			this.types.add(new SelectItem(type, type.toString()));
		}
	}
	
	public List<User> doSearchUser(String searchText) {
		return userService.searchByName(FaceContextUtils.getLoginUser().getOrganization(), searchText);
	}
	
	public void doSearch() {
		this.lstDocument = isoDocumentService.searchByCriteria(FaceContextUtils.getLoginUser().getOrganization(), this.criteria);
	}
	
	public void doReset() {
		this.criteria.setFrom(null);
		this.criteria.setTo(null);
		this.criteria.setType(null);
		this.criteria.setUser(null);
	}
	
	public void onRowToggle(ToggleEvent event) {
		if (event.getVisibility().equals(org.primefaces.model.Visibility.VISIBLE)) {
			IsoDocument doc = (IsoDocument) event.getData();
			List<UserActivity> activities = userActivityService.getDomainHistories(doc);
			doc.setActivities(activities);
		}
	}
}
