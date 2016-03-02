package com.iso.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.springframework.dao.DataAccessException;

import com.iso.domain.Category;
import com.iso.domain.IsoDocument;
import com.iso.exception.BusinessException;
import com.iso.model.IsoTrashDomain;
import com.iso.service.CategoryService;
import com.iso.service.IsoDocumentService;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="trashDomaintBean")
@ViewScoped
public class TrashDomaintBean implements Serializable{

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value="#{isoDocumentService}")
	private IsoDocumentService isoDocumentService;
	
	@ManagedProperty(value="#{categoryService}")
	private CategoryService categoryService;
	
	private List<IsoTrashDomain> lstTrash;
	private List<IsoTrashDomain> lstFilteredTrash;
	
	private String filterText;
	private IsoTrashDomain selectedTrash;
	
	public IsoDocumentService getIsoDocumentService() {
		return isoDocumentService;
	}

	public void setIsoDocumentService(IsoDocumentService isoDocumentService) {
		this.isoDocumentService = isoDocumentService;
	}

	public CategoryService getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public List<IsoTrashDomain> getLstTrash() {
		return lstTrash;
	}

	public void setLstTrash(List<IsoTrashDomain> lstTrash) {
		this.lstTrash = lstTrash;
		resetFilter();
	}

	public IsoTrashDomain getSelectedTrash() {
		return selectedTrash;
	}

	public void setSelectedTrash(IsoTrashDomain selectedTrash) {
		this.selectedTrash = selectedTrash;
	}
	
	public List<IsoTrashDomain> getLstFilteredTrash() {
		return lstFilteredTrash;
	}

	public void setLstFilteredTrash(List<IsoTrashDomain> lstFilteredTrash) {
		this.lstFilteredTrash = lstFilteredTrash;
	}

	public String getFilterText() {
		return filterText;
	}

	public void setFilterText(String filterText) {
		this.filterText = filterText;
	}

	public boolean isSelectedTrashItem() {
		return (this.selectedTrash != null && this.selectedTrash.getDomain() != null);
	}
	
	public boolean isSelectedDocument() {
		return (this.selectedTrash != null && this.selectedTrash.getDomain() != null && this.selectedTrash.getDomain() instanceof IsoDocument);
	}

	public boolean isSelectedCategory() {
		return (this.selectedTrash != null && this.selectedTrash.getDomain() != null && this.selectedTrash.getDomain() instanceof Category);
	}
	
	@PostConstruct
    public void initialize() {
		this.lstTrash = new ArrayList<IsoTrashDomain>();
		resetFilter();
    }
	
	private void resetFilter() {
		this.lstFilteredTrash = null;
		this.filterText = "";
		RequestContext.getCurrentInstance().execute("if (typeof PF('trashWidgetVar') !== 'undefined') { PF('trashWidgetVar').clearFilters();}");
		
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
    
    public void doRestoreTrashItem() {
    	try {
			if (isSelectedCategory()) {
				categoryService.restoreCategoryAndDocument(FaceContextUtils.getLoginUser(), (Category) this.selectedTrash.getDomain());
			} else if (isSelectedDocument()) {
				isoDocumentService.restoreCategoryAndDocument(FaceContextUtils.getLoginUser(), (IsoDocument) this.selectedTrash.getDomain());
			}
			
			Object bean = FaceContextUtils.getViewScopeBean("documentSearchBean");
			if (bean != null) {
				DocumentSearchBean searchBean = (DocumentSearchBean) bean;
				searchBean.doSearchTrash();
			}
			
			FaceContextUtils.showInfoMessage("Restore successfully", null);
    	} catch (BusinessException e) {
    		e.printStackTrace();
    		FaceContextUtils.showErrorMessage("Error when restore deleted item!", e.getStackTrace().toString());
    	}
	}
    
    public void doDeleteTrashItemPermanently() {
    	
    	try {
			if (isSelectedCategory()) {
				categoryService.delete(FaceContextUtils.getLoginUser(), (Category) this.selectedTrash.getDomain(), true);
			} else if (isSelectedDocument()) {
				isoDocumentService.deleteFilePermanent(FaceContextUtils.getLoginUser(), (IsoDocument) this.selectedTrash.getDomain());
			}
			
			Object bean = FaceContextUtils.getViewScopeBean("documentSearchBean");
			if (bean != null) {
				DocumentSearchBean searchBean = (DocumentSearchBean) bean;
				searchBean.doSearchTrash();
			}
			
			FaceContextUtils.showInfoMessage("Delete successfully", null);
    	} catch (BusinessException e) {
    		e.printStackTrace();
    		FaceContextUtils.showErrorMessage("Error when restore deleted item!", e.getStackTrace().toString());
    	}
    	
    }
}
