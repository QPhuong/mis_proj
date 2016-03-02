package com.iso.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.iso.domain.Category;
import com.iso.domain.CategorySecurity;
import com.iso.domain.Organization;
import com.iso.domain.OrganizationUnit;
import com.iso.domain.User;
import com.iso.service.CategoryService;
import com.iso.service.OrganizationUnitService;
import com.iso.service.UserService;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="categorySecurityBean")
@ViewScoped
public class CategorySecurityBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private CategorySecurity selectedSecurity;
	private Organization organization;
	private Category selectedCategory;
	private OrganizationUnit selectedOrgUnit;
	private User selectedUser;
	
	@ManagedProperty(value="#{organizationUnitService}")
	private OrganizationUnitService organizationUnitService;
	
	@ManagedProperty(value="#{userService}")
	private UserService userService;
	
	@ManagedProperty(value="#{categoryService}")
	private CategoryService categoryService;
	
	public OrganizationUnitService getOrganizationUnitService() {
		return organizationUnitService;
	}
	public void setOrganizationUnitService(OrganizationUnitService organizationUnitService) {
		this.organizationUnitService = organizationUnitService;
	}
	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	public OrganizationUnit getSelectedOrgUnit() {
		return selectedOrgUnit;
	}
	public void setSelectedOrgUnit(OrganizationUnit selectedOrgUnit) {
		this.selectedOrgUnit = selectedOrgUnit;
	}
	public User getSelectedUser() {
		return selectedUser;
	}
	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
	}
	public List<CategorySecurity> getSecurityLst() {
		return (this.selectedCategory == null || CollectionUtils.isEmpty(this.selectedCategory.getCategorySecurities())) ? new ArrayList<CategorySecurity>() : this.selectedCategory.getCategorySecurities();
	}
	public boolean isSelectedCategory() {
		return (this.selectedCategory != null && this.selectedCategory.getId() != null);
	}
	public CategoryService getCategoryService() {
		return categoryService;
	}
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	public CategorySecurity getSelectedSecurity() {
		return selectedSecurity;
	}
	public void setSelectedSecurity(CategorySecurity selectedSecurity) {
		this.selectedSecurity = selectedSecurity;
	}
	@PostConstruct
	public void initialize() {
		try{
			Organization organization = FaceContextUtils.getLoginUser().getOrganization();
			this.organization = organization;
			
			CategoryTreeBean categoryTreeBean = (CategoryTreeBean) FaceContextUtils.getViewScopeBean("categoryTreeBean");
			this.selectedCategory = categoryTreeBean.getSelectedCategory();
			
		}catch(Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when getting category security information", e.getStackTrace().toString());
		}
	}
	
	public void doReloadCategorySecurity() {
		CategoryTreeBean categoryTreeBean = (CategoryTreeBean) FaceContextUtils.getViewScopeBean("categoryTreeBean");
		if (categoryTreeBean.getSelectedCategory() != null && categoryTreeBean.getSelectedCategory().getId()!=null) {
			this.selectedCategory = categoryService.getCategoryAndSecurityById(categoryTreeBean.getSelectedCategory().getId().toString(), FaceContextUtils.getLoginUser());
		}else {
			this.selectedCategory = null;
		}
	}
	
	public List<OrganizationUnit> doSearchOrganizationUnit(String searchText) {
		return organizationUnitService.searchByName(this.organization, StringUtils.trim(searchText));
	}
	
	public List<User> doSearchUser(String searchText) {
		return userService.searchByName(this.organization, searchText);
	}
	
	private void saveSingleCategorySecurity(Category category, CategorySecurity security, OrganizationUnit orgUnit, User user, boolean isOverride) {
		
		boolean isNew = true;
		if (CollectionUtils.isEmpty(category.getCategorySecurities())) {
			category.setCategorySecurities(new ArrayList<CategorySecurity>());
		}
		for(int index = 0; index < category.getCategorySecurities().size(); index++) {
			CategorySecurity existing = category.getCategorySecurities().get(index);
			
			if (existing.getOrgUnit() != null && orgUnit != null && 
					orgUnit.getId().equals(existing.getOrgUnit().getId())) {
				isNew = false;
				if (isOverride) {
					category.getCategorySecurities().set(index, security);
				}
				break;
			}else if (existing.getUser() != null && user != null && 
					user.getId().equals(existing.getUser().getId())) {				
				isNew = false;
				if (isOverride) {
					category.getCategorySecurities().set(index, security);
				}
				break;
			}
		}
		if (isNew) {
			security.setOrgUnit(orgUnit);
			security.setUser(user);
			category.getCategorySecurities().add(security);
		}
		categoryService.saveCategorySecurity(FaceContextUtils.getLoginUser(), category);
	}
	
	private void addAllSubOrgUnitCategorySecurity(CategorySecurity security, OrganizationUnit orgUnit) {
		
		this.saveSingleCategorySecurity(this.selectedCategory, security, orgUnit, null, false);
		
		List<OrganizationUnit> subOrgUnits = organizationUnitService.getOrganizationChildByParent(this.organization, orgUnit);
		if (CollectionUtils.isNotEmpty(subOrgUnits)) {
			for (OrganizationUnit subOrgUnit: subOrgUnits) {
				addAllSubOrgUnitCategorySecurity(security.cloneCategorySecurity(), subOrgUnit);
			}
		}
		
	}

	/**
	 * Add a new category security with empty permissions
	 * @param event
	 */
	public void doAddNewCategorySecurity(ActionEvent event) {
		try {
			this.saveSingleCategorySecurity(this.selectedCategory, new CategorySecurity(), this.selectedOrgUnit, this.selectedUser, false);
			this.selectedOrgUnit = null;
			this.selectedUser = null;
			
			FaceContextUtils.showInfoMessage("Add category security successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when adding category security information", e.getStackTrace().toString());
		}		
	}	
	
	/**
	 * Save all category security permission in screen
	 */
	public void doSaveAllCategorySecurity() {
		try {
			categoryService.saveCategorySecurity(FaceContextUtils.getLoginUser(), this.selectedCategory);
			FaceContextUtils.showInfoMessage("Save category security successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when saving category security information", e.getStackTrace().toString());
		}
	}
	
	/**
	 * Reset all category security in screen to original
	 */
	public void doResetAllCategorySecurity() {
		try {
			this.selectedCategory = categoryService.getCategoryAndSecurityById(this.selectedCategory.getId().toString(), FaceContextUtils.getLoginUser());
			FaceContextUtils.showInfoMessage("Reset category security successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when reset category security information", e.getStackTrace().toString());
		}
	}
	
	/**
	 * Delete a category security
	 */
	public void doDeleteCategorySecurity() {
		try {
			this.selectedCategory.getCategorySecurities().remove(this.selectedSecurity);
			categoryService.saveCategorySecurity(FaceContextUtils.getLoginUser(), this.selectedCategory);
			FaceContextUtils.showInfoMessage("Reset category security successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when delete category security information", e.getStackTrace().toString());
		}
	}
	
	/**
	 * Add category security for an organization unit and all sub organization unit
	 */
	public void doAddAllSubOrgUnitCategorySecurity() {
		try {
			addAllSubOrgUnitCategorySecurity(new CategorySecurity(), this.selectedOrgUnit);
			
			this.selectedOrgUnit = null;
			this.selectedUser = null;
			
			FaceContextUtils.showInfoMessage("Add category security successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when adding category security information", e.getStackTrace().toString());
		}
	}
	
	private void applyInherit(CategorySecurity security, OrganizationUnit orgUnit) {
		
		OrganizationUnit parent = orgUnit.getParent();
		
		if (CollectionUtils.isNotEmpty(this.selectedCategory.getCategorySecurities())) {
			for (CategorySecurity existing: this.selectedCategory.getCategorySecurities()) {
				if (existing.getOrgUnit() != null && parent != null &&
						existing.getOrgUnit().getId().equals(parent.getId())) {
					security = existing.cloneCategorySecurity();
					break;
				}
			}
		}
		security.setOrgUnit(orgUnit);
		this.saveSingleCategorySecurity(this.selectedCategory, security, orgUnit, null, true);
	}
	
	public void doAddCategorySecurityInherit() {
		try {
			CategorySecurity subSecurity = new CategorySecurity();
			subSecurity.setOrgUnit(this.selectedOrgUnit);
			applyInherit(subSecurity, this.selectedOrgUnit);
			this.selectedOrgUnit = null;
			this.selectedUser = null;
			
			FaceContextUtils.showInfoMessage("Add category security successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when adding category security information", e.getStackTrace().toString());
		}
	}
	
	public void doApplyInheritSecurityFromParent() {
		try {
			applyInherit(this.selectedSecurity, this.selectedSecurity.getOrgUnit());
			this.selectedOrgUnit = null;
			this.selectedUser = null;
			
			FaceContextUtils.showInfoMessage("Save category security successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when adding category security information", e.getStackTrace().toString());
		}
	}
	
	public void doApplyToAllSubOrgUnit() {
		try {
			if (CollectionUtils.isNotEmpty(this.selectedCategory.getCategorySecurities())) {				
				for(int index = 0; index < this.selectedCategory.getCategorySecurities().size(); index++) {
					
					CategorySecurity security = this.selectedCategory.getCategorySecurities().get(index);
					if (security.getOrgUnit() != null && security.getOrgUnit().getParent() != null &&
							security.getOrgUnit().getParent().getId().equals(this.selectedSecurity.getOrgUnit().getId())) {
						CategorySecurity clone = this.selectedSecurity.cloneCategorySecurity();
						clone.setOrgUnit(security.getOrgUnit());
						this.selectedCategory.getCategorySecurities().set(index, clone);
					}
				}
			}
			
			categoryService.saveCategorySecurity(FaceContextUtils.getLoginUser(), this.selectedCategory);
			
			this.selectedOrgUnit = null;
			this.selectedUser = null;
			
			FaceContextUtils.showInfoMessage("Add category security successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when adding category security information", e.getStackTrace().toString());
		}
	}
	
	private void applySecurityForSubCategory(Category parent, CategorySecurity security) {
		List<Category> lstSubCategory = categoryService.getSubCategories(parent);
		
		if (CollectionUtils.isNotEmpty(lstSubCategory)) {				
			for (Category subCategory : lstSubCategory) {
				CategorySecurity clonedSecurity = security.cloneCategorySecurity();
				this.saveSingleCategorySecurity(subCategory, security, clonedSecurity.getOrgUnit(), clonedSecurity.getUser(), true);
				this.applySecurityForSubCategory(subCategory, clonedSecurity);
			}
		}
	}

	public void doApplyToAllSubCategory() {
		try {
			applySecurityForSubCategory(this.selectedCategory, this.selectedSecurity);
			
			categoryService.saveCategorySecurity(FaceContextUtils.getLoginUser(), this.selectedCategory);
			
			CategoryTreeBean categoryTreeBean = (CategoryTreeBean) FaceContextUtils.getViewScopeBean("categoryTreeBean");
			categoryTreeBean.doReloadCategoryTree();
			
			this.selectedOrgUnit = null;
			this.selectedUser = null;
			
			FaceContextUtils.showInfoMessage("Apply category security for all sub categories successfully", null);	
		}catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when adding category security information", e.getStackTrace().toString());
		}
	}
}
