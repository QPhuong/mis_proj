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

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.dao.DataAccessException;

import com.iso.domain.Category;
import com.iso.domain.IsoDocument;
import com.iso.model.IsoFile;
import com.iso.service.CategoryService;
import com.iso.service.IsoDocumentService;
import com.iso.service.impl.IsoDocumentServiceImpl;
import com.iso.util.FaceContextUtils;
import com.iso.util.IsoFileSupportUtils;
import com.mongodb.gridfs.GridFSDBFile;

@ManagedBean(name="categoryPropertiesBean")
@ViewScoped
public class CategoryPropertiesBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private Category selectedCategory;
	
	private IsoDocument isoDocument;
	private IsoFile temporaryFile;
	private List<IsoFile> lstTemporaryFile;
	
	private StreamedContent downloadFile;
	
	@ManagedProperty(value="#{categoryService}")
	private CategoryService categoryService;
	
	@ManagedProperty(value="#{isoDocumentService}")
	private IsoDocumentService isoDocumentService;
	
	public Category getSelectedCategory() {
		return selectedCategory;
	}
	public void setSelectedCategory(Category selectedCategory) {
		this.selectedCategory = selectedCategory;
	}
	public CategoryService getCategoryService() {
		return categoryService;
	}
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	public IsoDocumentService getIsoDocumentService() {
		return isoDocumentService;
	}
	public void setIsoDocumentService(IsoDocumentService isoDocumentService) {
		this.isoDocumentService = isoDocumentService;
	}
	public IsoDocument getIsoDocument() {
		return isoDocument;
	}
	public void setIsoDocument(IsoDocument isoDocument) {
		this.isoDocument = isoDocument;
	}
	public IsoFile getTemporaryFile() {
		return temporaryFile;
	}
	public void setTemporaryFile(IsoFile temporaryFile) {
		this.temporaryFile = temporaryFile;
	}
	public List<IsoFile> getLstTemporaryFile() {
		return lstTemporaryFile;
	}
	public void setLstTemporaryFile(List<IsoFile> lstTemporaryFile) {
		this.lstTemporaryFile = lstTemporaryFile;
	}
	public StreamedContent getDownloadFile() {
		return downloadFile;
	}
	public void setDownloadFile(StreamedContent downloadFile) {
		this.downloadFile = downloadFile;
	}
	
	@PostConstruct
	public void initialize() {
		try{
			doLoadCategoryProperties();
			this.lstTemporaryFile = new ArrayList<IsoFile>();
			
		}catch(DataAccessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when loading category properties!", null);
		}
	}
	
	public void doSaveProperties(ActionEvent event) {
		try {
			categoryService.saveCategoryPropertiesAndTemplates(FaceContextUtils.getLoginUser(), this.selectedCategory, this.lstTemporaryFile);
			this.lstTemporaryFile = new ArrayList<IsoFile>();
			
			FaceContextUtils.showInfoMessage("Save category properties successfully", null);
			
			Object bean = FaceContextUtils.getViewScopeBean("categoryTreeBean");
			if (bean != null) {
				CategoryTreeBean treeBean = (CategoryTreeBean) bean;
				treeBean.doReloadCategoryTree();
			}
			
		}catch(DataAccessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when saving category properties!", null);
		}
	}
	
	public void doResetProperties(ActionEvent event) {
		doLoadCategoryProperties();
	}
	
	public void doLoadCategoryProperties() {
		Object bean = FaceContextUtils.getViewScopeBean("categoryTreeBean");
		if (bean != null) {
			CategoryTreeBean treeBean = (CategoryTreeBean) bean;
			if (treeBean.getSelectedCategory() != null && treeBean.getSelectedCategory().getId() != null) {
				this.selectedCategory = categoryService.getCategoryAndSecurityById(treeBean.getSelectedCategory().getId().toString(), FaceContextUtils.getLoginUser());
				this.selectedCategory.setPath(IsoFileSupportUtils.getLocation(this.selectedCategory));
				this.selectedCategory.setSubCatNum(categoryService.getCatChildNumber(this.selectedCategory.getId().toString()));
				this.selectedCategory.setFileNum(isoDocumentService.countFilesByCategory(this.selectedCategory));
			}
			this.lstTemporaryFile = new ArrayList<IsoFile>();
		}else {
			FaceContextUtils.showErrorMessage("Error when loading category properties!", null);
		}
	}
	
	public void doLoadDeletedCategoryProperties() {
		Object bean = FaceContextUtils.getViewScopeBean("trashDomaintBean");
		if (bean != null) {
			TrashDomaintBean trashBean = (TrashDomaintBean) bean;
			if (trashBean.getSelectedTrash() != null && trashBean.getSelectedTrash().getDomain() != null) {
				if (trashBean.getSelectedTrash().getDomain() instanceof Category) {
					this.selectedCategory = (Category) trashBean.getSelectedTrash().getDomain();
					this.selectedCategory = categoryService.getCategoryAndSecurityById(this.selectedCategory.getId().toString(), FaceContextUtils.getLoginUser());
					this.selectedCategory.setPath(IsoFileSupportUtils.getLocation(this.selectedCategory));
					this.selectedCategory.setSubCatNum(categoryService.getCatChildNumber(this.selectedCategory.getId().toString()));
					this.selectedCategory.setFileNum(isoDocumentService.countFilesByCategory(this.selectedCategory));
				}
			}
			this.lstTemporaryFile = new ArrayList<IsoFile>();
		}else {
			FaceContextUtils.showErrorMessage("Error when loading category properties!", null);
		}
	}
	
	public void doUpload(FileUploadEvent  event) {
		try {
			UploadedFile uploadedFile = event.getFile();
			this.lstTemporaryFile.add(new IsoFile(uploadedFile));
		} catch (IOException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when reading uploaded file", e.getStackTrace().toString());
		}
	}
	
	public void doDeleteTemporaryFile() {
		this.lstTemporaryFile.remove(this.temporaryFile);
		this.temporaryFile = null;
	}
	
	public void doDeleteTemplateFile() {
		this.selectedCategory.getLstTemplates().remove(this.isoDocument);
		this.isoDocument = null;
	}
	
	public void doDownloadTemplate(ActionEvent event) {
    	try {
    		if (this.isoDocument != null && this.isoDocument.getId() != null) {
    			GridFSDBFile gridFile = isoDocumentService.getGridFSFile(FaceContextUtils.getLoginUser(), this.isoDocument);
    			this.downloadFile = new DefaultStreamedContent(gridFile.getInputStream(), gridFile.getContentType(), this.isoDocument.getFileName());
    		}else {
    			this.downloadFile = null;
    		}
    	} catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when download document!", e.getStackTrace().toString());
		}
    }
	
	public void doDownloadTempTemplate(ActionEvent event) {
    	try {
    		if (this.temporaryFile != null) {
    			this.downloadFile = new DefaultStreamedContent(this.temporaryFile.getInputStream(), this.temporaryFile.getContentType(), this.temporaryFile.getFileName());
    		}else {
    			this.downloadFile = null;
    		}
    	} catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when download document!", e.getStackTrace().toString());
		}
    }
}
