package com.iso.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.springframework.dao.DataAccessException;

import com.iso.domain.IsoDocument;
import com.iso.domain.UserActivity;
import com.iso.service.IsoDocumentService;
import com.iso.service.UserActivityService;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="documentHistoriesBean")
@ViewScoped
public class DocumentHistoriesBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private IsoDocument selectedDoc;
	private List<UserActivity> userActivities;
	
	@ManagedProperty(value="#{isoDocumentService}")
	private IsoDocumentService isoDocumentService;
	
	@ManagedProperty(value="#{userActivityService}")
	private UserActivityService userActivityService;
	
	public IsoDocument getSelectedDoc() {
		return selectedDoc;
	}

	public void setSelectedDoc(IsoDocument selectedDoc) {
		this.selectedDoc = selectedDoc;
	}

	public List<UserActivity> getUserActivities() {
		return userActivities;
	}

	public void setUserActivities(List<UserActivity> userActivities) {
		this.userActivities = userActivities;
	}

	public IsoDocumentService getIsoDocumentService() {
		return isoDocumentService;
	}

	public void setIsoDocumentService(IsoDocumentService isoDocumentService) {
		this.isoDocumentService = isoDocumentService;
	}

	public UserActivityService getUserActivityService() {
		return userActivityService;
	}

	public void setUserActivityService(UserActivityService userActivityService) {
		this.userActivityService = userActivityService;
	}

	@PostConstruct
	public void initialize() {
		try{
			doLoadDocumentHistories();
		}catch(DataAccessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when loading category properties!", null);
		}
	}
	
	public void doLoadDocumentHistories() {
		Object bean = FaceContextUtils.getViewScopeBean("documentBean");
		if (bean != null) {
			DocumentBean docBean = (DocumentBean) bean;
			this.selectedDoc = docBean.getSelectedDoc();
			this.userActivities = userActivityService.getDomainHistories(this.selectedDoc);
		}else {
			FaceContextUtils.showErrorMessage("Error when loading category properties!", null);
		}
	}
	
}
