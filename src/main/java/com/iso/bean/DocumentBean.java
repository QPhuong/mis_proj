package com.iso.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.dao.DataAccessException;

import com.iso.domain.IsoDocument;
import com.iso.exception.BusinessException;
import com.iso.model.IsoFile;
import com.iso.service.IsoDocumentService;
import com.iso.util.FaceContextUtils;
import com.mongodb.gridfs.GridFSDBFile;

@ManagedBean(name="documentBean")
@ViewScoped
public class DocumentBean implements Serializable{

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value="#{isoDocumentService}")
	private IsoDocumentService isoDocumentService;
	
	private List<IsoDocument> lstDocument;
	private List<IsoDocument> lstFilterDocument;
	
	private String filterText;
	
	private IsoDocument selectedDoc;
	private StreamedContent downloadFile;
	private boolean isOpenPreviewDoc = false;
	
	private boolean isMajorVersion;
	private String newVersionComment;
	private IsoFile tempFile;
	
	public List<IsoDocument> getLstDocument() {
		return lstDocument;
	}
	public void setLstDocument(List<IsoDocument> lstDocument) {
		this.lstDocument = lstDocument;
		resetFilter();
	}
	public IsoDocument getSelectedDoc() {
		return selectedDoc;
	}
	public void setSelectedDoc(IsoDocument selectedDoc) {
		this.selectedDoc = selectedDoc;
	}
	public IsoDocumentService getIsoDocumentService() {
		return isoDocumentService;
	}
	public void setIsoDocumentService(IsoDocumentService isoDocumentService) {
		this.isoDocumentService = isoDocumentService;
	}
    public StreamedContent getDownloadFile() {
		return downloadFile;
	}
	public void setDownloadFile(StreamedContent downloadFile) {
		this.downloadFile = downloadFile;
	}
	public boolean isMajorVersion() {
		return isMajorVersion;
	}
	public void setMajorVersion(boolean isMajorVersion) {
		this.isMajorVersion = isMajorVersion;
	}
	public String getNewVersionComment() {
		return newVersionComment;
	}
	public void setNewVersionComment(String newVersionComment) {
		this.newVersionComment = newVersionComment;
	}
	public boolean isUploaledFile() {
		return this.tempFile != null;
	}
	public IsoFile getTempFile() {
		return tempFile;
	}
	public void setTempFile(IsoFile tempFile) {
		this.tempFile = tempFile;
	}
	public boolean isOpenPreviewDoc() {
		return isOpenPreviewDoc;
	}
	public void setOpenPreviewDoc(boolean isOpenPreviewDoc) {
		this.isOpenPreviewDoc = isOpenPreviewDoc;
	}
	public List<IsoDocument> getLstFilterDocument() {
		return lstFilterDocument;
	}
	public void setLstFilterDocument(List<IsoDocument> lstFilterDocument) {
		this.lstFilterDocument = lstFilterDocument;
	}
	public boolean isSelectedDocElement() {
    	return (this.selectedDoc != null && this.selectedDoc.getId() != null);
    }
	public String getFilterText() {
		return filterText;
	}
	public void setFilterText(String filterText) {
		this.filterText = filterText;
	}
	
	@PostConstruct
    public void initialize() {
		setLstDocument(new ArrayList<IsoDocument>());
		this.lstFilterDocument = null;
    }
	
	private void resetFilter() {
		this.lstFilterDocument = null;
		this.filterText = "";
		RequestContext.getCurrentInstance().execute("if (typeof PF('documentWidgetVar') !== 'undefined') { PF('documentWidgetVar').clearFilters();}");
		
		
	}
	
	public void doRefreshDocLst(ActionEvent event) {
    	try {
    		CategoryTreeBean categoryStructureBean = (CategoryTreeBean) FaceContextUtils.getViewScopeBean("categoryTreeBean");
    		if (categoryStructureBean.getSelectedCategory() != null && categoryStructureBean.getSelectedCategory().getId() != null) {
    			setLstDocument(isoDocumentService.getFilesByCateogry(categoryStructureBean.getSelectedCategory()));
	    		boolean canSelectingDocument = false;
	    		if (CollectionUtils.isNotEmpty(lstDocument)) {
	    			for (IsoDocument file : lstDocument) {
	    				if (this.selectedDoc != null && file.getId().equals(this.selectedDoc.getId())) {
	    					canSelectingDocument = true;
	    					this.selectedDoc = file;
	    				}
	    			}
	    		}
	    		
	    		if(!canSelectingDocument) {
	    			this.selectedDoc = null;
	    		}
    		} else {
    			setLstDocument(new ArrayList<IsoDocument>());
    			this.selectedDoc = null ;
    		}
    		
    		resetFilter();
			FaceContextUtils.showInfoMessage("Refesh Successfully!", null);
		}catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when refresh document list!", e.getStackTrace().toString());
		}
    }
    
    public void doUploadNewDocument(FileUploadEvent e) {
    	try {
    		// Get uploaded file from the FileUploadEvent
            this.tempFile = new IsoFile(e.getFile());
            CategoryTreeBean categoryStructureBean = (CategoryTreeBean) FaceContextUtils.getViewScopeBean("categoryTreeBean");
			isoDocumentService.uploadNewFile(FaceContextUtils.getLoginUser(), this.tempFile, categoryStructureBean.getSelectedCategory());
	    	
	    	FaceContextUtils.showInfoMessage("Files are uploaded successfully", null);
	    	
	    	this.tempFile = null;
    	} catch (IOException ex) {
    		ex.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when uploading document!", ex.getStackTrace().toString());
		} catch (BusinessException e1) {
			e1.printStackTrace();
			FaceContextUtils.showErrorMessage(e1.getMessage(), e1.getStackTrace().toString());
		}
    }    

    public void doDeleteDocument(ActionEvent event) {
    	try {
    		isoDocumentService.deleteFile(FaceContextUtils.getLoginUser(), selectedDoc);
    		this.selectedDoc = null;
    		
			FaceContextUtils.showInfoMessage("Delete Successfully!", null);
		}catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when deleting document!", e.getStackTrace().toString());
		}
    }
    
    public void doDownloadFile(ActionEvent event) {
    	try {
    		GridFSDBFile gridFile = isoDocumentService.getGridFSFile(FaceContextUtils.getLoginUser(), this.selectedDoc);
    		this.downloadFile = new DefaultStreamedContent(gridFile.getInputStream(), gridFile.getContentType(), this.selectedDoc.getFileName());            
    	} catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when download document!", e.getStackTrace().toString());
		}
    }
    
    public void onRowSelect(SelectEvent event) {
    	try {
    		
    	} catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when loading document information!", e.getStackTrace().toString());
		}
    }
 
    public void onRowUnselect(UnselectEvent event) {
    	try {
    		
    	} catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when loading document information!", e.getStackTrace().toString());
		}
    }
    
    public void doOpenCheckInDialog() {
    }
    
    public void doUploadNewDocumentVersion(FileUploadEvent e) {
		try {
			this.tempFile = new IsoFile(e.getFile());
		} catch (IOException e1) {
			e1.printStackTrace();
			FaceContextUtils.showErrorMessage(e1.getMessage(), e1.getStackTrace().toString());
		}
	}
    
    public void doCheckInNewDocumentVersion() {
    	try {
    		isoDocumentService.checkedInNewFileVersion(FaceContextUtils.getLoginUser(), this.selectedDoc, this.tempFile, isMajorVersion, newVersionComment);
	    	this.tempFile = null;
	    	
	    	FaceContextUtils.showInfoMessage("File Uploaded Successfully", null);
    	} catch (Exception ex) {
    		ex.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when uploading document: " + ex.getMessage(), ex.getStackTrace().toString());
		}
    }
    
    public void doCheckOut() {
    	try {
    		isoDocumentService.checkedOutIsoDocument(FaceContextUtils.getLoginUser(), this.selectedDoc);
    		
    		FaceContextUtils.showInfoMessage("Check out document successfully", null);
    	} catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when loading document information!", e.getStackTrace().toString());
		}
    }
    
    public void doLocked() {
    	try {
    		isoDocumentService.lockedIsoDocument(FaceContextUtils.getLoginUser(), this.selectedDoc);
    		
    		FaceContextUtils.showInfoMessage("Lock document successfully", null);
    	} catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when loading document information!", e.getStackTrace().toString());
		}
    }
    
    public void doUnlocked() {
    	try {
    		isoDocumentService.unlockedIsoDocument(FaceContextUtils.getLoginUser(), this.selectedDoc);
    		
    		FaceContextUtils.showInfoMessage("Unlock document successfully", null);
    	} catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when loading document information!", e.getStackTrace().toString());
		}
    }
    
    public void doOpenPreviewDocument() {
    	this.isOpenPreviewDoc = true;
    }
    
    public void doClosePreviewDocument() {
    	this.isOpenPreviewDoc = false;
    }
    
    
}
