package com.iso.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.TabChangeEvent;

import com.iso.comparator.IsoTrashDomainComparator;
import com.iso.constant.DocumentSearchCriteria;
import com.iso.constant.DocumentSearchCriteria.DocumentSearchType;
import com.iso.domain.Category;
import com.iso.domain.IsoDocument;
import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.elasticsearch.configuration.ElasticsearchConfiguration;
import com.iso.model.IsoTrashDomain;
import com.iso.service.CategoryService;
import com.iso.service.DocumentElasticsearchService;
import com.iso.service.IsoDocumentService;
import com.iso.service.UserService;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="documentSearchBean")
@ViewScoped
public class DocumentSearchBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int activeDocDetailTabIndex = 0;
	
	private final int DOC_PROPERTIES_TAB_INDEX = 0;
	private final int DOC_EXT_PROPERTIES_TAB_INDEX = 1;
	private final int DOC_HISTORIES_TAB_INDEX = 2;
	private final int DOC_VERSIONS_TAB_INDEX = 3;
	
	private DocumentSearchCriteria searchCriteria;
	private boolean basicSearch;
	private boolean searchMyDocument;
	private List<IsoDocument> lstDocument;
	
	private int activeSearchType = 0;
	
	private final int SEARCH_DOCUMENT = 0;
	private final int SEARCH_TRASH = 1;
	
	private DocumentSearchCriteria trashCriteria;
	private boolean searchMyDeletedDocument;
	private List<IsoTrashDomain> lstTrashItem;

	@ManagedProperty(value="#{isoDocumentService}")
	private IsoDocumentService isoDocumentService;
	
	@ManagedProperty(value="#{categoryService}")
	private CategoryService categoryService;
	
	@ManagedProperty(value="#{userService}")
	private UserService userService;
	
	@ManagedProperty(value="#{documentElasticsearchService}")
	private DocumentElasticsearchService documentElasticsearchService;
	
	public int getActiveDocDetailTabIndex() {
		return activeDocDetailTabIndex;
	}

	public void setActiveDocDetailTabIndex(int activeDocDetailTabIndex) {
		this.activeDocDetailTabIndex = activeDocDetailTabIndex;
	}

	public List<IsoDocument> getLstDocument() {
		return lstDocument;
	}

	public void setLstDocument(List<IsoDocument> lstDocument) {
		this.lstDocument = lstDocument;
	}

	public DocumentSearchCriteria getSearchCriteria() {
		return searchCriteria;
	}

	public void setSearchCriteria(DocumentSearchCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	public boolean isBasicSearch() {
		return basicSearch;
	}

	public void setBasicSearch(boolean basicSearch) {
		this.basicSearch = basicSearch;
	}

	public boolean isSearchMyDocument() {
		return searchMyDocument;
	}

	public void setSearchMyDocument(boolean searchMyDocument) {
		this.searchMyDocument = searchMyDocument;
	}

	public IsoDocumentService getIsoDocumentService() {
		return isoDocumentService;
	}

	public void setIsoDocumentService(IsoDocumentService isoDocumentService) {
		this.isoDocumentService = isoDocumentService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public int getActiveSearchType() {
		return activeSearchType;
	}

	public void setActiveSearchType(int activeSearchType) {
		this.activeSearchType = activeSearchType;
	}

	public DocumentSearchCriteria getTrashCriteria() {
		return trashCriteria;
	}

	public void setTrashCriteria(DocumentSearchCriteria trashCriteria) {
		this.trashCriteria = trashCriteria;
	}

	public boolean isSearchMyDeletedDocument() {
		return searchMyDeletedDocument;
	}

	public void setSearchMyDeletedDocument(boolean searchMyDeletedDocument) {
		this.searchMyDeletedDocument = searchMyDeletedDocument;
	}

	public List<IsoTrashDomain> getLstTrashItem() {
		return lstTrashItem;
	}

	public void setLstTrashItem(List<IsoTrashDomain> lstTrashItem) {
		this.lstTrashItem = lstTrashItem;
	}

	public CategoryService getCategoryService() {
		return categoryService;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public DocumentElasticsearchService getDocumentElasticsearchService() {
		return documentElasticsearchService;
	}

	public void setDocumentElasticsearchService(
			DocumentElasticsearchService documentElasticsearchService) {
		this.documentElasticsearchService = documentElasticsearchService;
	}

	@PostConstruct
	private void initialize() {
		this.lstDocument = new ArrayList<IsoDocument>();
		this.lstTrashItem = new ArrayList<IsoTrashDomain>();
		
		this.searchCriteria = new DocumentSearchCriteria();
		this.searchCriteria.setType(DocumentSearchType.NEW);
		this.searchCriteria.setOrganization(FaceContextUtils.getLoginUser().getOrganization());
		
		this.trashCriteria = new DocumentSearchCriteria();
		this.trashCriteria.setType(DocumentSearchType.DELETE);
		this.trashCriteria.setOrganization(FaceContextUtils.getLoginUser().getOrganization());
		
		this.basicSearch = true;
		this.searchMyDocument = false;
	}
	
	/************************** For layout render - Start *********************************/
	public void onSearchTabChange(TabChangeEvent event) {
		this.activeSearchType = event.getComponent().getChildren().indexOf(event.getTab());
	}
	
	public void onDocumentDetailTabChange(TabChangeEvent event) {
		this.activeDocDetailTabIndex = event.getComponent().getChildren().indexOf(event.getTab());
		this.doDetailTabRefresh();
	}
	
	public void doDetailTabRefresh() {
		if (isRenderSearchDocResult()) {
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
			}
		} else if (isRenderSearchTrashResult()){
			if (isRenderDocPropertiesTabContent()) {
				Object bean = FaceContextUtils.getViewScopeBean("documentPropertiesBean");
				if (bean != null) {
					DocumentPropertiesBean docPropBean = (DocumentPropertiesBean) bean;
					docPropBean.doLoadDeletedDocumentProperties();
				} 
			} else if (isRenderCategoryPropertiesTabContent()) {
				Object bean = FaceContextUtils.getViewScopeBean("categoryPropertiesBean");
				if (bean != null) {
					CategoryPropertiesBean catPropBean = (CategoryPropertiesBean) bean;
					catPropBean.doLoadDeletedCategoryProperties();;
				} 
			}
		}
	}
	
	public boolean isRenderSearchDocResult() {
		return (this.activeSearchType == SEARCH_DOCUMENT);
	}

	public boolean isRenderSearchTrashResult() {
		return (this.activeSearchType == SEARCH_TRASH);
	}
	
	public boolean isRenderDocumentDetailMainTab() {
		Object bean = FaceContextUtils.getViewScopeBean("documentBean");
		if (bean != null) {
			DocumentBean docBean = (DocumentBean) bean;
			return docBean.isSelectedDocElement() && isRenderSearchDocResult();
		} else {
			return false;
		}
	}
	
	public boolean isRenderTrashDetailMainTab() {
		Object bean = FaceContextUtils.getViewScopeBean("trashDomaintBean");
		if (bean != null) {
			TrashDomaintBean docBean = (TrashDomaintBean) bean;
			return docBean.isSelectedTrashItem() && isRenderSearchTrashResult();
		} else {
			return false;
		}
	}
	
	public boolean isRenderCategoryPropertiesTabContent() {
		if (isRenderSearchTrashResult()) {
			Object bean = FaceContextUtils.getViewScopeBean("trashDomaintBean");
			if (bean != null) {
				TrashDomaintBean docBean = (TrashDomaintBean) bean;
				return docBean.isSelectedCategory();
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean isRenderDocPropertiesTabContent(){
		if (isRenderSearchDocResult()) {
			return isRenderDocumentDetailMainTab() && (this.activeDocDetailTabIndex == DOC_PROPERTIES_TAB_INDEX);
		}else if (isRenderSearchTrashResult()) {
			Object bean = FaceContextUtils.getViewScopeBean("trashDomaintBean");
			if (bean != null) {
				TrashDomaintBean docBean = (TrashDomaintBean) bean;
				return docBean.isSelectedDocument();
			} else {
				return false;
			}
		}
		return false;
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
	
	/************************** For layout render - End *********************************/
	
	private void refreshResultList() {
		if (this.activeSearchType == SEARCH_DOCUMENT) {
			Object bean = FaceContextUtils.getViewScopeBean("documentBean");
			if (bean != null) {
				DocumentBean docBean = (DocumentBean) bean;
				docBean.setLstDocument(this.lstDocument);
				docBean.setLstFilterDocument(this.lstDocument);
				docBean.setSelectedDoc(null);			
			}
		} else if (this.activeSearchType == SEARCH_TRASH) {
			Object bean = FaceContextUtils.getViewScopeBean("trashDomaintBean");
			if (bean != null) {
				TrashDomaintBean docBean = (TrashDomaintBean) bean;
				docBean.setLstTrash(this.lstTrashItem);
				docBean.setSelectedTrash(null);		
			}
		}
	}
	
	public List<User> doSearchUser(String searchText) {
		return userService.searchByName(FaceContextUtils.getLoginUser().getOrganization(), searchText);
	}
	
	public void doSearch() {
		
		try {this.searchCriteria.setType(DocumentSearchType.NEW);
			if (this.searchMyDocument) {
				this.searchCriteria.setUser(FaceContextUtils.getLoginUser());
			}
		
			if(Boolean.parseBoolean(ElasticsearchConfiguration.getPropertiesValues(ElasticsearchConfiguration.ELASTICSEARCH_ACTIVE))) {
				lstDocument = documentElasticsearchService.searchDocument(this.searchCriteria, this.basicSearch);
			} else {
				lstDocument = isoDocumentService.searchByCriteria(FaceContextUtils.getLoginUser().getOrganization(), this.searchCriteria);
			}
			
			refreshResultList();
		} catch (IOException e) {
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
			e.printStackTrace();
		}
	}
	
	public void doSwitchSearchMode() {
		this.basicSearch = !this.basicSearch;
	}
	
	public void doSearchTrash() {
		this.trashCriteria.setType(DocumentSearchType.DELETE);
		Organization organization = FaceContextUtils.getLoginUser().getOrganization();
		
		List<IsoDocument> lstDeletedDocument = isoDocumentService.searchByCriteria(organization, this.trashCriteria);
		List<Category> lstDeletedCategory = categoryService.searchByCriteria(organization, this.trashCriteria);
		
		this.lstTrashItem = new ArrayList<IsoTrashDomain>();
		
		User loginUser = FaceContextUtils.getLoginUser();
		
		if (CollectionUtils.isNotEmpty(lstDeletedCategory)) {
			for (Category category : lstDeletedCategory) {
				lstTrashItem.add(new IsoTrashDomain(category, loginUser));
			}
		}
		if (CollectionUtils.isNotEmpty(lstDeletedDocument)) {
			for (IsoDocument document : lstDeletedDocument) {
				lstTrashItem.add(new IsoTrashDomain(document, loginUser));
			}
		}
		
		Collections.sort(this.lstTrashItem, new IsoTrashDomainComparator());
		
		refreshResultList();
	}
}
