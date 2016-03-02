package com.iso.service.impl;

import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.iso.constant.AdministratorActivityLogs;
import com.iso.constant.LogTypes;
import com.iso.constant.Messages;
import com.iso.domain.MainCategory;
import com.iso.domain.MainCategoryType;
import com.iso.domain.User;
import com.iso.domain.UserActivity;
import com.iso.domain.generic.DomainObjectVersion;
import com.iso.exception.BusinessException;
import com.iso.repository.MainCategoryTypeRepository;
import com.iso.service.MainCategoryService;
import com.iso.service.MainCategoryTypeService;
import com.iso.service.UserActivityService;

@Component(value="mainCategoryTypeService")
public class MainCategoryTypeServiceImpl implements MainCategoryTypeService{

	private static final long serialVersionUID = 1L;

	@Autowired
	private MainCategoryTypeRepository mainCategoryTypeRepo;
	
	@Autowired
	@Qualifier(value="mainCategoryService")
	private MainCategoryService mainCategoryService;
	
	@Autowired
	@Qualifier(value="userActivityService")
	private UserActivityService userActivityService;
	
	
	/*public MainCategoryType saveGenerateMainCategoriesStatus(User loginUser, MainCategoryType categoryType) throws BusinessException {
		return save(loginUser, categoryType, AdministratorActivityLogs.MAIN_CATEGORY_TYPE_MODIFRY);
	}*/
	
	public MainCategoryType save(User loginUser, MainCategoryType categoryType) throws BusinessException {
		if (categoryType.getId() == null) {
			return save(loginUser, categoryType, AdministratorActivityLogs.MAIN_CATEGORY_TYPE_INSERT_NEW);
		}else {
			return save(loginUser, categoryType, AdministratorActivityLogs.MAIN_CATEGORY_TYPE_MODIFRY);
		}
	}
	
	private MainCategoryType save(User loginUser, MainCategoryType categoryType, String logDetail) throws BusinessException {
		MainCategoryType original = null;
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.ADMIN_MAIN_CATEGORIES_MANAGEMENT_LOG);
		
		if (categoryType.getId() == null) {
			if(mainCategoryTypeRepo.findByCode(categoryType.getCode()) != null) {
				throw new BusinessException(Messages.DUPLICATE_DOCUEMNT_MAIN_CATEGORY_TYPE_CODE);
			}
			logActivity.setActiviy(AdministratorActivityLogs.MAIN_CATEGORY_TYPE_INSERT_NEW);
			
		}else {
			logActivity.setActiviy(AdministratorActivityLogs.MAIN_CATEGORY_TYPE_MODIFRY);
			original = mainCategoryTypeRepo.findOne(categoryType.getId().toString());
		}
		
		categoryType = mainCategoryTypeRepo.save(categoryType);
		
		logActivity.setDomainRef(categoryType);
		logActivity.setDomainVersion(new DomainObjectVersion<MainCategoryType>(MainCategoryType.class, original, categoryType));
		userActivityService.save(logActivity);
		
		return categoryType;
	}
	
	public void delete(User loginUser, MainCategoryType categoryType) throws BusinessException {
		MainCategory rootMainCategory = mainCategoryService.getRootCategoryByCategoryType(categoryType);
		
		if (rootMainCategory != null) {
			mainCategoryService.delete(loginUser, rootMainCategory);
		}
		mainCategoryTypeRepo.delete(categoryType);
		
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.ADMIN_MAIN_CATEGORIES_MANAGEMENT_LOG);
		logActivity.setActiviy(AdministratorActivityLogs.MAIN_CATEGORY_TYPE_DELETE);
		logActivity.setDomainVersion(new DomainObjectVersion<MainCategoryType>(MainCategoryType.class, categoryType, null));
		logActivity.setDomainRef(categoryType);
		userActivityService.save(logActivity);
	}
	
	@SuppressWarnings("unchecked")
	public List<MainCategoryType> getAllMainCategoryType() {
		return IteratorUtils.toList(mainCategoryTypeRepo.findAll().iterator());
	}
	
	public List<MainCategoryType> getAllValidMainCategoryTypes() {
		return mainCategoryTypeRepo.findValidMainCategoryTypes();
	}
	
	public MainCategoryType getById(String id) {
		return mainCategoryTypeRepo.findOne(id);
	}
	
	public MainCategoryType getByCode(String code) {
		return mainCategoryTypeRepo.findByCode(code);
	}
	
}
