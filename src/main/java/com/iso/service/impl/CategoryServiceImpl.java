package com.iso.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.iso.constant.AdministratorActivityLogs;
import com.iso.constant.CategoryActivityLogs;
import com.iso.constant.CategorySortValue;
import com.iso.constant.DocumentSearchCriteria;
import com.iso.constant.LogTypes;
import com.iso.constant.SortType;
import com.iso.domain.Category;
import com.iso.domain.CategorySecurity;
import com.iso.domain.IsoDocument;
import com.iso.domain.MainCategory;
import com.iso.domain.MainCategoryType;
import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.domain.UserActivity;
import com.iso.domain.generic.DomainObjectVersion;
import com.iso.exception.BusinessException;
import com.iso.model.IsoFile;
import com.iso.repository.CategoryRepository;
import com.iso.repository.IsoDocumentRepository;
import com.iso.service.CategoryService;
import com.iso.service.IsoDocumentService;
import com.iso.service.MainCategoryService;
import com.iso.service.OrganizationService;
import com.iso.service.UserActivityService;
import com.iso.util.DomainSecurityChecker;
import com.mongodb.gridfs.GridFSFile;

@Component(value="categoryService")
public class CategoryServiceImpl implements CategoryService{	
	
	private static final long serialVersionUID = 1L;
	
	@Autowired
	@Qualifier(value="isoDocumentService")
	IsoDocumentService documentService;
	
	@Autowired
	IsoDocumentRepository isoDocumentRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	@Qualifier(value="organizationService")
	OrganizationService organizationService;
	
	@Autowired
	@Qualifier(value="mainCategoryService")
	MainCategoryService mainCategoryService;
	
	@Autowired
	@Qualifier(value="userActivityService")
	private UserActivityService userActivityService;
	
	public Category addNewCategory(User loginUser, Category category) {
		
		Category parent = getCategoryAndSecurityById(category.getParent().getId().toString(), loginUser);
		if (parent != null) {
			List<CategorySecurity> security = new ArrayList<CategorySecurity>();	
			security.addAll(parent.getCategorySecurities());
			category.setCategorySecurities(security);
			category.setFinalSecurity(parent.getFinalSecurity().cloneCategorySecurity());
		}
		return save(loginUser, category, null, null, CategoryActivityLogs.CREATE_CAT, "");
	}
	
	public Category saveChangedCategory(User loginUser, Category category) {
		return save(loginUser, category, null, null, CategoryActivityLogs.MODIFY_CAT, "");
	}
	
	public Category saveMovedCategory(User loginUser, Category category) {
		return save(loginUser, category, null, null, CategoryActivityLogs.MOVE_CAT, "");
	}
	
	public Category saveCategoryProperties(User loginUser, Category category) {
		return save(loginUser, category, null, null, CategoryActivityLogs.MODIFY_CAT_PROPERTIES, "");
	}
	
	public Category saveCategoryPropertiesAndTemplates(User loginUser, Category category, List<IsoFile> lstUploadedFile) {
		return save(loginUser, category, lstUploadedFile, null, CategoryActivityLogs.MODIFY_CAT_PROPERTIES, "");
	}
	
	public Category saveCategoryExtProperties(User loginUser, Category category) {
		return save(loginUser, category, null, null, CategoryActivityLogs.MODIFY_CAT_EXT_PROPERTIES, "");
	}
	
	public Category saveCategorySecurity(User loginUser, Category category) {
		return save(loginUser, category, null, null, CategoryActivityLogs.MODIFY_CAT_SECURITY, "");
	}
	
	private Category save(User loginUser, Category category, List<IsoFile> lstUploadedFile, List<IsoDocument> lstTemplate, String log, String comment) {
		Category original = null;
		
		
		if (category.getId() != null) {
			original = categoryRepository.findOne(category.getId().toString());
			category.setUpdatedBy(loginUser);
			category.setUpdatedOn(Calendar.getInstance().getTime());
			
			if (CollectionUtils.isNotEmpty(original.getLstTemplates())) {
				//delete removed template
				List<IsoDocument> removedTemplates = new ArrayList<IsoDocument>();
				removedTemplates.addAll(original.getLstTemplates());

				if (CollectionUtils.isNotEmpty(category.getLstTemplates())) {
					//filter removed templates
					for(IsoDocument source : original.getLstTemplates()) {
						for(IsoDocument existing : category.getLstTemplates()) {
							if (source.getId().equals(existing.getId())) {
								removedTemplates.remove(source);
							}
						}
					}
				}
				
				if (CollectionUtils.isNotEmpty(removedTemplates)) {
					for(IsoDocument template : removedTemplates) {
						documentService.deleteFilePermanent(loginUser, template);
					}
				}
			}
			
		}
		
		if (category.getId() == null) {
			category.setCreatedBy(loginUser);
			category.setCreatedOn(Calendar.getInstance().getTime());
			category = categoryRepository.save(category);
		}
		
		if (CollectionUtils.isNotEmpty(lstUploadedFile)) {
			for (IsoFile uploadFile : lstUploadedFile) {
				//insert gridFS File and IsoDocument
				IsoDocument file = new IsoDocument();
				file.setOrganization(loginUser.getOrganization());
				file.setCategory(category);
				file.setTemplate(true);
				file.setCreatedBy(loginUser);
				file.setCreatedOn(Calendar.getInstance().getTime());
				
				GridFSFile gridfsFile = isoDocumentRepository.insertGridTemplateFile
							(uploadFile.getInputStream(), uploadFile.getFileName(), uploadFile.getContentType(), loginUser.getOrganization());
				
				file = isoDocumentRepository.insertIsoDocument(file, gridfsFile);
				if (category.getLstTemplates() == null) {
					category.setLstTemplates(new ArrayList<IsoDocument>());
				}
				category.getLstTemplates().add(file);
			}
		}
		
		if (CollectionUtils.isNotEmpty(lstTemplate)) {
			//add templates of main category
			for (IsoDocument template : lstTemplate) {
				//insert gridFS File and IsoDocument
				IsoDocument file = new IsoDocument();
				file.setOrganization(loginUser.getOrganization());
				file.setCategory(category);
				file.setTemplate(true);
				file.setCreatedBy(loginUser);
				file.setCreatedOn(Calendar.getInstance().getTime());
				
				GridFSFile gridfsFile = isoDocumentRepository.cloneGridFile(template);
				file = isoDocumentRepository.insertIsoDocument(file, gridfsFile);
				if (category.getLstTemplates() == null) {
					category.setLstTemplates(new ArrayList<IsoDocument>());
				}
				category.getLstTemplates().add(file);
			}
		}
		
		category = categoryRepository.save(category);
		
		//store log
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.CATEGORY);
		logActivity.setActiviy(log);
		logActivity.setDomainRef(category);
		logActivity.setComment(comment);
		logActivity.setDomainVersion(new DomainObjectVersion<Category>(Category.class, original, category));
		userActivityService.save(logActivity);
		
		return category;
	}
	
	public void delete(User loginUser, Category category, boolean deletePermanent) throws BusinessException {
		
		Category original = categoryRepository.findOne(category.getId().toString());;
		
		List<Category> subLst = categoryRepository.findByParentCategoryIdOrderByNameAsc(category.getId().toString());
		if (!CollectionUtils.isEmpty(subLst)) {
			for (Category sub : subLst) {
				this.delete(loginUser, sub, deletePermanent);
			}
		}
		
		if (deletePermanent) {
			categoryRepository.delete(category);
		}else {
			category.setDeleted(true);
			category.setDeletedBy(loginUser);
			category.setDeletedOn(Calendar.getInstance().getTime());
			categoryRepository.save(category);
		}
		
		//delete documents in category
		List<IsoDocument> lstDocument = documentService.getFilesByCateogry(category);
		if (CollectionUtils.isNotEmpty(lstDocument)) {
			for (IsoDocument document : lstDocument) {
				if (deletePermanent) {
					documentService.deleteFilePermanent(loginUser, document);
				}else {
					documentService.deleteFile(loginUser, document);
				}
			}
		}
		
		//delete uploaded templates
		List<IsoDocument> lstTemplate = category.getLstTemplates();
		if (CollectionUtils.isNotEmpty(lstTemplate)) {
			for (IsoDocument document : lstTemplate) {
				if (deletePermanent) {
					documentService.deleteFilePermanent(loginUser, document);
				}else {
					documentService.deleteFile(loginUser, document);
				}
			}
		}
		
		if (category.getParent() == null) {
			Organization organization = category.getOrganization();
			organization.setLocked(false);
			organization = organizationService.saveOrganizationData(loginUser, organization, AdministratorActivityLogs.ORGANIZATION_UNLOCKED);
			loginUser.setOrganization(organization);
		}
		
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.CATEGORY);
		logActivity.setActiviy(CategoryActivityLogs.DELETE_CAT);
		logActivity.setDomainRef(category);
		logActivity.setDomainVersion(new DomainObjectVersion<Category>(Category.class, original, category));
		userActivityService.save(logActivity);
	}
			
	public Category getCategoryById(String id) {
		return categoryRepository.findOne(id);
	}
	
	public Category getCategoryAndSecurityById(String id, User user) {
		Category category = categoryRepository.findOne(id);
		category.setFinalSecurity(DomainSecurityChecker.checkCategorySecurity(user, category));
		return category;
	}
	
	public Category getRootCategoryByOrganization(String organizationId) {
		Category root = categoryRepository.findRootCategoryByOrganization(organizationId);
		return root;
	}
	
	public Category getRootCategoryAndSecurityByOrganization(String organizationId, User user) {
		Category root = categoryRepository.findRootCategoryByOrganization(organizationId);
		if (root != null) {
			root.setFinalSecurity(DomainSecurityChecker.checkCategorySecurity(user, root));
		}
		return root;
	}
	
	public long getCatChildNumber(String parentId) {
		return categoryRepository.countChildByParentId(parentId);
	}
	
	public List<Category> getSubCategories(Category parent) {		
		List<Category> categoryLst = categoryRepository.findByParentCategoryIdOrderByNameAsc(parent.getId().toString());
		return categoryLst;
	}
	
	public List<Category> getSubCategories(Category category, SortType sortType, CategorySortValue sortValue) {
		List<Category> categoryLst = categoryRepository.findByParentCategory(category.getId().toString(), sortType, sortValue);
		return categoryLst;
	}
	
	private Category saveNewCategoryAndTemplate(User loginUser, Category category, List<IsoDocument> lstTemplate) {
		return save(loginUser, category, null, lstTemplate, CategoryActivityLogs.CREATE_CAT, "");
	}
	
	private Category createSubCategoryListByMainCategory(User loginUser, MainCategory mainCategory, Category parent) {
		Category category = new Category(mainCategory);
		category.setParent(parent);
		category.setOrganization(parent.getOrganization());
		category = this.saveNewCategoryAndTemplate(loginUser, category, mainCategory.getLstTemplates());
		
		List<MainCategory> subMainCategories = mainCategoryService.getSubMainCategoryByParent(mainCategory, CategorySortValue.DATE_CREATED, SortType.ASC);
		if(CollectionUtils.isNotEmpty(subMainCategories)) {
			for (MainCategory subMainCategory : subMainCategories) {
				createSubCategoryListByMainCategory(loginUser, subMainCategory, category);
			}
		}
		
		return category;
	}
	
	public Category createCategoriesByMainCategoryType(User loginUser, MainCategoryType mainCategoryType, Organization organization) throws BusinessException {
		
		//Delete all old categories
		Category oldRootCategory = getRootCategoryByOrganization(organization.getId().toString());
		if (oldRootCategory != null && oldRootCategory.getId() != null) {
			this.delete(loginUser, oldRootCategory, true);
		}
		
		//insert new root category
		MainCategory rootMainCategory = mainCategoryService.getRootCategoryByCategoryType(mainCategoryType);
		Category rootCategory = new Category(rootMainCategory);	
		rootCategory.setOrganization(organization);
		rootCategory.setRoot(true);
		rootCategory = this.saveNewCategoryAndTemplate(loginUser, rootCategory, rootMainCategory.getLstTemplates());
		
		//update organization
		organization.setLocked(false);
		organization = organizationService.saveOrganizationData(loginUser, organization, AdministratorActivityLogs.ORGANIZATION_GENERATE_CATEGORIES);
		
		//save all sub categories of root category
		List<MainCategory> subMainCategories = mainCategoryService.getSubMainCategoryByParent(rootMainCategory, CategorySortValue.DATE_CREATED, SortType.ASC);
		if(CollectionUtils.isNotEmpty(subMainCategories)) {
			for (MainCategory subMainCategory : subMainCategories) {
				createSubCategoryListByMainCategory(loginUser, subMainCategory, rootCategory);
			}
		}
		
		return rootCategory;
	}
	
	public List<Category> searchByCriteria(Organization organization, DocumentSearchCriteria criteria) {
		return categoryRepository.searchByCriteria(organization, criteria);
	}
	
	private void restoreAll(User loginUser, Category category) throws BusinessException {
			
		Category original = categoryRepository.findOne(category.getId().toString());;
		
		List<Category> subLst = categoryRepository.findDeletedCategoryByParent(category.getId().toString());
		if (!CollectionUtils.isEmpty(subLst)) {
			for (Category sub : subLst) {
				this.restoreAll(loginUser, sub);
			}
		}
		category.setDeleted(false);
		category.setDeletedBy(null);
		category.setDeletedOn(null);
		categoryRepository.save(category);
		
		//restore document
		List<IsoDocument> lstDocument = documentService.getDeletedFileByCategory(category);
		if (CollectionUtils.isNotEmpty(lstDocument)) {
			for (IsoDocument document : lstDocument) {
				documentService.restoreDocument(loginUser, document);
			}
		}
		
		//restore template
		List<IsoDocument> lstTemplate = category.getLstTemplates();
		if (CollectionUtils.isNotEmpty(lstTemplate)) {
			for (IsoDocument document : lstTemplate) {
				documentService.restoreDocument(loginUser, document);
			}
		}
		
		if (category.getParent() == null) {
			Organization organization = category.getOrganization();
			organization.setLocked(true);
			organization = organizationService.saveOrganizationData(loginUser, organization, AdministratorActivityLogs.ORGANIZATION_LOCKED);
			loginUser.setOrganization(organization);
		}
		
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.CATEGORY);
		logActivity.setActiviy(CategoryActivityLogs.RESTORE_CATEGORIES);
		logActivity.setDomainRef(category);
		logActivity.setDomainVersion(new DomainObjectVersion<Category>(Category.class, original, category));
		userActivityService.save(logActivity);
	}
	
	private void restoreOnlyCategory(User loginUser, Category category) throws BusinessException {
		
		Category original = categoryRepository.findOne(category.getId().toString());;
		
		List<Category> subLst = categoryRepository.findDeletedCategoryByParent(category.getId().toString());
		if (!CollectionUtils.isEmpty(subLst)) {
			for (Category sub : subLst) {
				if (sub.isDeleted()) {
					this.restoreOnlyCategory(loginUser, sub);
				}
			}
		}
		//categoryRepository.delete(category);
		category.setDeleted(false);
		category.setDeletedBy(null);
		category.setDeletedOn(null);
		categoryRepository.save(category);
		
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.CATEGORY);
		logActivity.setActiviy(CategoryActivityLogs.RESTORE_CATEGORIES);
		logActivity.setDomainRef(category);
		logActivity.setDomainVersion(new DomainObjectVersion<Category>(Category.class, original, category));
		userActivityService.save(logActivity);
	}
	
	public void restoreCategoryAndDocument(User loginUser, Category category) throws BusinessException {
		if (category.getParent() != null && category.getParent().isDeleted()) {
			restoreCategoryAndDocument(loginUser, category.getParent());
		} else {
			restoreAll(loginUser, category);
		}
	}
	
	public void restoreCategory(User loginUser, Category category) throws BusinessException {
		if (category.getParent() != null && category.getParent().isDeleted()) {
			restoreCategory(loginUser, category.getParent());
		} else {
			restoreOnlyCategory(loginUser, category);
		}
	}
}
