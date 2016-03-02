package com.iso.repository;

import java.util.List;

import com.iso.constant.CategorySortValue;
import com.iso.constant.DocumentSearchCriteria;
import com.iso.constant.SortType;
import com.iso.domain.Category;
import com.iso.domain.Organization;

public interface CategoryRepositoryCustom {

	public List<Category> findDeletedCategoryByParent(String parentId);
	public List<Category> findByParentCategoryIdOrderByNameAsc(String parentId); 
	public List<Category> findByParentCategory(String parentId, SortType sortType, CategorySortValue sortValue);

	public List<Category> findByCategorySecurityOrganizationUnit(String organizationUnitId, String organizationId);
	public List<Category> findByCategorySecurityUser(String userId, String organizationId);
	
	public Category findRootCategoryByOrganization(String organizationId);
	
	public long countChildByParentId(String id);
	
	public List<Category> searchByCriteria(Organization organization, DocumentSearchCriteria criteria);
	public List<Category> searchByControlNumber(String controlNumber, String organizationId);
}
