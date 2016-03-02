package com.iso.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.TabChangeEvent;

import com.iso.domain.Category;
import com.iso.domain.IsoDocument;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="categoryDetailLayoutBean")
@ViewScoped
public class CategoryDetailLayoutBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final int CAT_PROPERTIES_TAB_INDEX = 0;
	private final int CAT_EXT_PROPERTIES_TAB_INDEX = 1;
	private final int CAT_SECURITY_TAB_INDEX = 2;
	private final int CAT_HISTORY_TAB_INDEX = 3;
	
	
	private final int DOC_PROPERTIES_TAB_INDEX = 0;
	private final int DOC_EXT_PROPERTIES_TAB_INDEX = 1;
	private final int DOC_NOTES_TAB_INDEX = 2;
	private final int DOC_SECURITY_TAB_INDEX = 3;
	private final int DOC_VERSIONS_TAB_INDEX = 4;
	private final int DOC_HISTORIES_TAB_INDEX = 5;
	
	private int activeDocDetailTabIndex = 0;
	private int activeCategoryDetailTabIndex = 0;
	
	private Category selectedCategory;
	private IsoDocument selectedFile;

	public int getActiveDocDetailTabIndex() {
		return activeDocDetailTabIndex;
	}
	public void setActiveDocDetailTabIndex(int activeDocDetailTabIndex) {
		this.activeDocDetailTabIndex = activeDocDetailTabIndex;
	}
	public int getActiveCategoryDetailTabIndex() {
		return activeCategoryDetailTabIndex;
	}
	public void setActiveCategoryDetailTabIndex(int activeCategoryDetailTabIndex) {
		this.activeCategoryDetailTabIndex = activeCategoryDetailTabIndex;
	}

	@PostConstruct
	private void initialize() {
		if (isSelectedCateogry()) {
			DocumentBean docBean = (DocumentBean) FaceContextUtils.getViewScopeBean("documentBean");
			if (docBean != null) {
				docBean.doRefreshDocLst(null);
			}
		}
	}
	/**********************************************************************************************/
	/******************** Check is category selected / is document selected ? *********************/
	/**********************************************************************************************/
	public boolean isSelectedCateogry() {
		Object bean = FaceContextUtils.getViewScopeBean("categoryTreeBean");
		if (bean != null) {
			CategoryTreeBean categoryTreeBean = (CategoryTreeBean) bean;
			return categoryTreeBean.isSelectCategoryNode();
		} else {
			return false;
		}
	}
	
	public boolean isSelectedFile() {
		Object bean = FaceContextUtils.getViewScopeBean("documentBean");
		if (bean != null) {
			DocumentBean docBean = (DocumentBean) bean;
			return docBean.isSelectedDocElement();
		} else {
			return false;
		}
	}
	
	private Category getSeletedCategory() {
		Object treeBean = FaceContextUtils.getViewScopeBean("categoryTreeBean");
		if (treeBean != null) {
			CategoryTreeBean categoryTreeBean = (CategoryTreeBean) treeBean;
			this.selectedCategory = categoryTreeBean.getSelectedCategory();
		}
		return this.selectedCategory;
	}
	
	private IsoDocument getSelectedFile() {
		Object bean = FaceContextUtils.getViewScopeBean("documentBean");
		if (bean != null) {
			DocumentBean docBean = (DocumentBean) bean;
			this.selectedFile = docBean.getSelectedDoc();
		} 
		return this.selectedFile;
	}
	
	/************************************************************************************************/
	/****************************** Tab Event and Refresh Page***************************************/
	/************************************************************************************************/
	public void onDocumentTabChange(TabChangeEvent event) {
		this.activeDocDetailTabIndex = event.getComponent().getChildren().indexOf(event.getTab());
		this.doDocumentTabRefresh();
	}
	
	public void onCategoryTabChange(TabChangeEvent event) {
		this.activeCategoryDetailTabIndex = event.getComponent().getChildren().indexOf(event.getTab());
		this.doCategoryTabRefresh();
	}
	
	public void doRefresh() {
		
		DocumentBean docBean = (DocumentBean) FaceContextUtils.getViewScopeBean("documentBean");
		if (docBean != null) {
			docBean.doRefreshDocLst(null);
		}
		
		if (isRenderCategoryDetailMainTab()) {
			doCategoryTabRefresh();
		} else if(isRenderDocumentDetailMainTab()) {
			doDocumentTabRefresh();
		}
	}

	public void doDocumentTabRefresh() {
		
		if (this.activeDocDetailTabIndex == DOC_PROPERTIES_TAB_INDEX) {
			Object bean = FaceContextUtils.getViewScopeBean("documentPropertiesBean");
			if (bean != null) {
				DocumentPropertiesBean docPropBean = (DocumentPropertiesBean) bean;
				docPropBean.doLoadDocumentProperties();
			} 
		} else if (this.activeDocDetailTabIndex == DOC_EXT_PROPERTIES_TAB_INDEX) {
			Object bean = FaceContextUtils.getViewScopeBean("documentExtPropertiesBean");
			if (bean != null) {
				DocumentExtPropertiesBean propBean = (DocumentExtPropertiesBean) bean;
				propBean.doReloaExtProperties();
			}
		} else if (this.activeDocDetailTabIndex == DOC_HISTORIES_TAB_INDEX) {
			Object bean = FaceContextUtils.getViewScopeBean("documentHistoriesBean");
			if (bean != null) {
				DocumentHistoriesBean historiesBean = (DocumentHistoriesBean) bean;
				historiesBean.doLoadDocumentHistories();
			}
		} else if (this.activeDocDetailTabIndex == DOC_VERSIONS_TAB_INDEX) {
			Object bean = FaceContextUtils.getViewScopeBean("documentVersionsBean");
			if (bean != null) {
				DocumentVersionsBean historiesBean = (DocumentVersionsBean) bean;
				historiesBean.doLoadDocumentVersions();
			}
		} else if (this.activeDocDetailTabIndex == DOC_NOTES_TAB_INDEX) {
			Object bean = FaceContextUtils.getViewScopeBean("documentNotesBean");
			if (bean != null) {
				DocumentNotesBean notesBean = (DocumentNotesBean) bean;
				notesBean.doLoadDocumentNotes();
			}
		} else if (this.activeDocDetailTabIndex == DOC_SECURITY_TAB_INDEX) {
			Object bean = FaceContextUtils.getViewScopeBean("documentSecurityBean");
			if (bean != null) {
				DocumentSecurityBean notesBean = (DocumentSecurityBean) bean;
				notesBean.doReloadDocumentSecurity();
			}
		}
	}
	
	public void doCategoryTabRefresh() {
		
		if (this.activeCategoryDetailTabIndex == CAT_PROPERTIES_TAB_INDEX) {
			Object bean = FaceContextUtils.getViewScopeBean("categoryPropertiesBean");
			if (bean != null) {
				CategoryPropertiesBean propBean = (CategoryPropertiesBean) bean;
				propBean.doLoadCategoryProperties();
			}
			
		}else if (this.activeCategoryDetailTabIndex == CAT_EXT_PROPERTIES_TAB_INDEX) {
			Object bean = FaceContextUtils.getViewScopeBean("categoryExtPropertiesBean");
			if (bean != null) {
				CategoryExtPropertiesBean propBean = (CategoryExtPropertiesBean) bean;
				propBean.doReloaExtProperties();
			}
			
		}else if (this.activeCategoryDetailTabIndex == CAT_SECURITY_TAB_INDEX) { //category security tab
			CategoryTreeBean treeBean = (CategoryTreeBean) FaceContextUtils.getViewScopeBean("categoryTreeBean");
			if (treeBean != null) {
				//Select a node in tree and have permission to setup the category security
				if (treeBean.getSelectedCategory() != null && treeBean.getSelectedCategory().getFinalSecurity().isCanSetupSecurity()) {
					CategorySecurityBean secBean = (CategorySecurityBean) FaceContextUtils.getViewScopeBean("categorySecurityBean");
					if (secBean != null) {
						secBean.doReloadCategorySecurity();
					}
				}else {
					this.activeCategoryDetailTabIndex = 0;
					this.doRefresh();
				}	
			}
		} else if (this.activeCategoryDetailTabIndex == CAT_HISTORY_TAB_INDEX) {
			Object bean = FaceContextUtils.getViewScopeBean("categoryHistoriesBean");
			if (bean != null) {
				CategoryHistoriesBean historiesBean = (CategoryHistoriesBean) bean;
				historiesBean.doLoadCategoryHistories();
			}
		}
	}
	
	/************************************************************************************************/
	/**************************Render category tab / document tab ***********************************/
	/************************************************************************************************/
	public boolean isRenderDocumentDetailMainTab() {
		
		boolean isRender = this.isSelectedCateogry() && this.isSelectedFile();
		if (getSelectedFile() != null && this.selectedFile.getId() != null) {
			isRender = isRender && this.selectedFile.canViewDocInfo(FaceContextUtils.getLoginUser());
		}
		return isRender ;
	}
	
	public boolean isRenderCategoryDetailMainTab() {
		return this.isSelectedCateogry() && !this.isSelectedFile();
	}
	
	public boolean isRenderCategorySecurityTab() {
		boolean isRender = isRenderCategoryDetailMainTab();
		
		boolean isCanSetupCategorySecurity = false;
		if (getSeletedCategory() != null && this.selectedCategory.getId() != null) {
			isCanSetupCategorySecurity = this.selectedCategory.getFinalSecurity().isCanSetupSecurity();
		}
		isRender = isRender && isCanSetupCategorySecurity;
		return isRender;
	}
	
	public boolean isRenderDocSecurityTab() {
		boolean isRender = isRenderDocumentDetailMainTab();
		
		boolean isCanSetupDocumentSecurity = false;
		if (getSelectedFile() != null && this.selectedFile.getId() != null) {
			isCanSetupDocumentSecurity = this.selectedFile.getFinalSecurity().isCanSetupSecurity();
		}
		isRender = isRender && isCanSetupDocumentSecurity;
		return isRender;
	}
	
	public boolean isRenderCategoryPropertiesTabContent() {
		return isRenderCategoryDetailMainTab() && (this.activeCategoryDetailTabIndex == CAT_PROPERTIES_TAB_INDEX);
	}
	
	public boolean isRenderCategoryExtPropertiesTabContent() {
		return isRenderCategoryDetailMainTab() && (this.activeCategoryDetailTabIndex == CAT_EXT_PROPERTIES_TAB_INDEX);
	}
	
	public boolean isRenderCategorySecurityTabContent() {
		boolean isRender = isRenderCategoryDetailMainTab() && isRenderCategorySecurityTab();
		isRender = isRender && (this.activeCategoryDetailTabIndex == CAT_SECURITY_TAB_INDEX);
		return isRender;
	}
	
	public boolean isRenderCategoryHistoryTabContent() {
		return isRenderCategoryDetailMainTab() && (this.activeCategoryDetailTabIndex == CAT_HISTORY_TAB_INDEX);
	}
	
	
	public boolean isRenderDocPropertiesTabContent(){
		return isRenderDocumentDetailMainTab() && (this.activeDocDetailTabIndex == DOC_PROPERTIES_TAB_INDEX);
	}
	
	public boolean isRenderDocExtPropertiesTabContent() {
		return isRenderDocumentDetailMainTab() && (this.activeDocDetailTabIndex == DOC_EXT_PROPERTIES_TAB_INDEX);
	}
	
	public boolean isRenderDocHistoriesTabContent() {
		return isRenderDocumentDetailMainTab() && (this.activeDocDetailTabIndex == DOC_HISTORIES_TAB_INDEX);
	}
	
	public boolean isRenderDocVersionsTabContent() {
		return isRenderDocumentDetailMainTab() && (this.activeDocDetailTabIndex == DOC_VERSIONS_TAB_INDEX);
	}
	
	public boolean isRenderDocNotesTabContent() {
		return isRenderDocumentDetailMainTab() && (this.activeDocDetailTabIndex == DOC_NOTES_TAB_INDEX);
	}
	
	public boolean isRenderDocSecurityTabContent() {
		boolean isRender = isRenderDocumentDetailMainTab() && isRenderDocSecurityTab();
		isRender = isRender && (this.activeDocDetailTabIndex == DOC_SECURITY_TAB_INDEX);
		return isRender; 
	}
	
	/************************************************************************************************/
	
}
