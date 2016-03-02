package com.iso.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.dao.DataAccessException;

import com.iso.service.IsoDocumentService;
import com.iso.util.FaceContextUtils;
import com.mongodb.gridfs.GridFSDBFile;

@ManagedBean(name="documentPreviewBean")
@RequestScoped
public class DocumentPreviewBean {

	@ManagedProperty(value="#{isoDocumentService}")
	private IsoDocumentService isoDocumentService;
	
	public IsoDocumentService getIsoDocumentService() {
		return isoDocumentService;
	}

	public void setIsoDocumentService(IsoDocumentService isoDocumentService) {
		this.isoDocumentService = isoDocumentService;
	}

	public StreamedContent getPreviewDocumentContent() {
    	try {
    		FacesContext context = FacesContext.getCurrentInstance();

    	    if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
    	    	return new DefaultStreamedContent();
    	    } else {
    	    	String id = context.getExternalContext().getRequestParameterMap().get("id");
    	    	if (StringUtils.isNotEmpty(id)) {
	    			GridFSDBFile gridFile = isoDocumentService.getGridFSFile(FaceContextUtils.getLoginUser(), id);
	    			return new DefaultStreamedContent(gridFile.getInputStream(), gridFile.getContentType(), gridFile.getFilename());
    	    	}
	    			
    	    }
    	} catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when download document!", e.getStackTrace().toString());
		}
    	return new DefaultStreamedContent();
    }
}
