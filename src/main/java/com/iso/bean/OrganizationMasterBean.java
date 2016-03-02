package com.iso.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.springframework.dao.DataAccessException;

import com.iso.domain.Category;
import com.iso.domain.MainCategoryType;
import com.iso.domain.Organization;
import com.iso.exception.BusinessException;
import com.iso.service.CategoryService;
import com.iso.service.MainCategoryTypeService;
import com.iso.service.OrganizationService;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="orgMasterBean")
@ViewScoped
public class OrganizationMasterBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private List<MainCategoryType> categoryTypes;
	private MainCategoryType selectedCategoryType;
	
	private Organization selectedOrg;
	private List<Organization> orgLst;
	private List<Organization> orgFilterLst;
	
	private String filterText;
	
	private boolean isShownDetail = false;
	
	@ManagedProperty(value="#{organizationService}")
	private OrganizationService organizationService;	
	
	@ManagedProperty(value="#{mainCategoryTypeService}")
	private MainCategoryTypeService mainCategoryTypeService;
	
	@ManagedProperty(value="#{categoryService}")
	private CategoryService categoryService;
	
	public OrganizationMasterBean(){
		this.selectedOrg = new Organization();
	}
	public Organization getSelectedOrg() {
		return selectedOrg;
	}
	public void setSelectedOrg(Organization selectedOrg) {
		this.selectedOrg = selectedOrg;
	}
	public List<Organization> getOrgLst() {
		return orgLst;
	}
	public void setOrgLst(List<Organization> orgLst) {
		this.orgLst = orgLst;
		resetFilter();
	}
	public boolean isShownDetail() {
		return isShownDetail;
	}
	public void setShownDetail(boolean isShownDetail) {
		this.isShownDetail = isShownDetail;
	}
	public OrganizationService getOrganizationService() {
		return organizationService;
	}
	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}
	public List<MainCategoryType> getCategoryTypes() {
		return categoryTypes;
	}
	public void setCategoryTypes(List<MainCategoryType> categoryTypes) {
		this.categoryTypes = categoryTypes;
	}
	public MainCategoryType getSelectedCategoryType() {
		return selectedCategoryType;
	}
	public void setSelectedCategoryType(MainCategoryType selectedCategoryType) {
		this.selectedCategoryType = selectedCategoryType;
	}
	public MainCategoryTypeService getMainCategoryTypeService() {
		return mainCategoryTypeService;
	}
	public void setMainCategoryTypeService(MainCategoryTypeService mainCategoryTypeService) {
		this.mainCategoryTypeService = mainCategoryTypeService;
	}
	public CategoryService getCategoryService() {
		return categoryService;
	}
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	public List<Organization> getOrgFilterLst() {
		return orgFilterLst;
	}
	public void setOrgFilterLst(List<Organization> orgFilterLst) {
		this.orgFilterLst = orgFilterLst;
	}
	public String getFilterText() {
		return filterText;
	}
	public void setFilterText(String filterText) {
		this.filterText = filterText;
	}
	
	@PostConstruct
	public void initialize() {
		try{
			doLoadAllOrganization();
			this.categoryTypes = mainCategoryTypeService.getAllValidMainCategoryTypes();
			if(CollectionUtils.isNotEmpty(this.categoryTypes)) {
				this.selectedCategoryType = this.categoryTypes.get(0);
			}
		}catch(DataAccessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when search Organization!",null);
		}
	}
	
	private void resetFilter(){
		this.orgFilterLst = null;
		this.filterText = "";
		RequestContext.getCurrentInstance().execute("if (typeof PF('organizationWidgetVar') !== 'undefined') { PF('organizationWidgetVar').clearFilters();}");
	}
	
	private void doLoadAllOrganization() {
		
		setOrgLst(organizationService.getAllOrganization());
		
		if (CollectionUtils.isNotEmpty(this.orgLst)) {
			for(Organization org : this.orgLst) {
			Category rootCategory = categoryService.getRootCategoryByOrganization(org.getId().toString());
			org.setRootCategory(rootCategory);
			
				if (rootCategory != null && rootCategory.getId() != null) {
					if (org.isLocked()) {
						org.setStatus("ui-icon-folder-red-locked");
					} else {
						org.setStatus("ui-icon-folder-green-locked");
					}
				} else {
					org.setStatus("ui-icon-folder-blue");
				}
			}
		}
	}
	
	/**
	 * Save (new and update) Organization detail
	 * @param event
	 */
	public void  doOrgDetailSave(ActionEvent event) {
		try {
			organizationService.saveOrganizationData(FaceContextUtils.getLoginUser(), selectedOrg);
			selectedOrg = new Organization();
			
			doLoadAllOrganization();
			setShownDetail(false);
			
			FaceContextUtils.showInfoMessage("Save Successfully!", null);
			
		}catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when saving user profile!",null);
			
		}catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(),null);
		}
	}

	/**
	 * Search Organization by criteria
	 * @param event
	 */
	public void doOrgSearch(ActionEvent event) {
		try{
			doLoadAllOrganization();
		}catch(DataAccessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when search user!",null);
		}
	}
	
	/**
	 * Delete Organization
	 * @param event
	 */
	public void doOrgDelete(ActionEvent event) {
		try {
			organizationService.delete(FaceContextUtils.getLoginUser(), selectedOrg);
			
			doLoadAllOrganization();
			selectedOrg = new Organization();
			
			FaceContextUtils.showInfoMessage("Delete Successfully!", null);
	        
		} catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when delete user profile!",null);
		} catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
		}
	}
	
	public void doOrgDetailEdit() {
		setShownDetail(true);
	}
	
	public void doOrgDetailCancel(){
		selectedOrg = new Organization();
		setShownDetail(false);
	}
	
	public void doAddNewOrg() {
		setShownDetail(true);
		this.selectedOrg = new Organization();
	}

	public void doGenerateCategories() {
		try {
			categoryService.createCategoriesByMainCategoryType(FaceContextUtils.getLoginUser(), this.selectedCategoryType, this.selectedOrg);
			doLoadAllOrganization();
			
			FaceContextUtils.showInfoMessage("Generate categories successfully!", null);
		} catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when generate categories for this organization: " + e.getMessage(), e.getStackTrace().toString());
		} catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
		}
	}
}
