package com.iso.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iso.constant.CategorySortValue;
import com.iso.constant.DocumentSearchCriteria;
import com.iso.constant.SortType;
import com.iso.domain.Category;
import com.iso.domain.MainCategoryType;
import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.exception.BusinessException;
import com.iso.model.IsoFile;

@Service
public interface CategoryService extends Serializable{

	public Category addNewCategory(User loginUser, Category category);
	
	public Category saveChangedCategory(User loginUser, Category category);
	
	public Category saveMovedCategory(User loginUser, Category category);
	
	public Category saveCategoryProperties(User loginUser, Category category);
	
	public Category saveCategoryPropertiesAndTemplates(User loginUser, Category category, List<IsoFile> lstUploadedFile);
	
	public Category saveCategoryExtProperties(User loginUser, Category category);
	
	public Category saveCategorySecurity(User loginUser, Category category);
	
	public void delete(User loginUser, Category category, boolean deletePermanent) throws BusinessException;
	
	public Category getCategoryById(String id);
	
	public Category getCategoryAndSecurityById(String id, User user) ;
	
	public Category getRootCategoryByOrganization(String organizationId);
	
	public Category getRootCategoryAndSecurityByOrganization(String organizationId, User user);
	
	public long getCatChildNumber(String parentId);
	
	public List<Category> getSubCategories(Category parent);
	
	public List<Category> getSubCategories(Category category, SortType sortType, CategorySortValue sortValue);
	
	public Category createCategoriesByMainCategoryType(User loginUser, MainCategoryType mainCategoryType, Organization organization) throws BusinessException;
	
	public List<Category> searchByCriteria(Organization organization, DocumentSearchCriteria criteria);
	
	public void restoreCategoryAndDocument(User loginUser, Category category) throws BusinessException;
	
	public void restoreCategory(User loginUser, Category category) throws BusinessException;
}
