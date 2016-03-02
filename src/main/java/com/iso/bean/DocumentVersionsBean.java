package com.iso.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.springframework.dao.DataAccessException;

import com.iso.domain.DocumentVersion;
import com.iso.domain.IsoDocument;
import com.iso.service.DocumentVersionService;
import com.iso.service.IsoDocumentService;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="documentVersionsBean")
@ViewScoped
public class DocumentVersionsBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private IsoDocument selectedDoc;
	private List<DocumentVersion> docVersions;
	
	@ManagedProperty(value="#{isoDocumentService}")
	private IsoDocumentService isoDocumentService;
	
	@ManagedProperty(value="#{documentVersionService}")
	private DocumentVersionService docVersionService;
	
	public IsoDocument getSelectedDoc() {
		return selectedDoc;
	}

	public void setSelectedDoc(IsoDocument selectedDoc) {
		this.selectedDoc = selectedDoc;
	}

	public List<DocumentVersion> getDocVersions() {
		return docVersions;
	}

	public void setDocVersions(List<DocumentVersion> docVersions) {
		this.docVersions = docVersions;
	}

	public IsoDocumentService getIsoDocumentService() {
		return isoDocumentService;
	}

	public void setIsoDocumentService(IsoDocumentService isoDocumentService) {
		this.isoDocumentService = isoDocumentService;
	}

	public DocumentVersionService getDocVersionService() {
		return docVersionService;
	}

	public void setDocVersionService(DocumentVersionService docVersionService) {
		this.docVersionService = docVersionService;
	}

	@PostConstruct
	public void initialize() {
		try{
			doLoadDocumentVersions();
		}catch(DataAccessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when loading category properties!", null);
		}
	}
	
	public void doLoadDocumentVersions() {
		Object bean = FaceContextUtils.getViewScopeBean("documentBean");
		if (bean != null) {
			DocumentBean docBean = (DocumentBean) bean;
			this.selectedDoc = docBean.getSelectedDoc();
			this.docVersions = docVersionService.getDocumentVersion(selectedDoc);
		}else {
			FaceContextUtils.showErrorMessage("Error when loading category properties!", null);
		}
	}
	
}
