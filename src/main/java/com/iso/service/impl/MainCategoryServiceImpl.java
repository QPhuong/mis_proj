package com.iso.service.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.iso.constant.AdministratorActivityLogs;
import com.iso.constant.CategorySortValue;
import com.iso.constant.LogTypes;
import com.iso.constant.SortType;
import com.iso.domain.IsoDocument;
import com.iso.domain.MainCategory;
import com.iso.domain.MainCategoryType;
import com.iso.domain.User;
import com.iso.domain.UserActivity;
import com.iso.domain.generic.DomainObjectVersion;
import com.iso.exception.BusinessException;
import com.iso.model.IsoFile;
import com.iso.repository.IsoDocumentRepository;
import com.iso.repository.MainCategoryRepository;
import com.iso.service.MainCategoryService;
import com.iso.service.MainCategoryTypeService;
import com.iso.service.UserActivityService;
import com.iso.util.FaceContextUtils;
import com.mongodb.gridfs.GridFSFile;

@Component(value="mainCategoryService")
public class MainCategoryServiceImpl implements MainCategoryService{

	private static final long serialVersionUID = 1L;
	
	
	@Autowired
	private MainCategoryRepository mainCatRepo;
	@Autowired
	private IsoDocumentRepository documentRepository;
	
	@Autowired
	@Qualifier(value="mainCategoryTypeService")
	private MainCategoryTypeService mainCategoryTypeService;
	
	@Autowired
	@Qualifier(value="userActivityService")
	private UserActivityService userActivityService;
	
	@Autowired
	private IsoDocumentRepository isoDocumentRepository;
	
	public MainCategory getMainCategoryById(String id) {
		return mainCatRepo.findOne(id);
	}
	
	public List<MainCategory> getSubMainCategoryByParent(MainCategory parent, CategorySortValue sortValue, SortType sortType) {
		
		if(sortValue.compareTo(CategorySortValue.NAME)  == 0) {
			if (sortType.compareTo(SortType.ASC) == 0) {
				return mainCatRepo.findByParentOrderByNameAsc(parent);
			} else if (sortType.compareTo(SortType.DESC) == 0){
				return mainCatRepo.findByParentOrderByNameDesc(parent);
			}
		} else if(sortValue.compareTo(CategorySortValue.DATE_CREATED)  == 0) {
			if (sortType.compareTo(SortType.ASC) == 0) {
				return mainCatRepo.findByParentOrderByCreatedOnAsc(parent);
			} else if (sortType.compareTo(SortType.DESC) == 0){
				return mainCatRepo.findByParentOrderByCreatedOnDesc(parent);
			}
		} else if(sortValue.compareTo(CategorySortValue.DATE_UPDATED)  == 0) {
			if (sortType.compareTo(SortType.ASC) == 0) {
				return mainCatRepo.findByParentOrderByUpdatedOnAsc(parent);
			} else if (sortType.compareTo(SortType.DESC) == 0){
				return mainCatRepo.findByParentOrderByUpdatedOnDesc(parent);
			}
		}
		return mainCatRepo.findByParentOrderByNameAsc(parent);
	}
	
	public MainCategory save(User loginUser, MainCategory category, List<IsoFile> uploadedFiles) throws IOException, BusinessException {
		if (category.getId() == null) {
			return save(loginUser, category, uploadedFiles, LogTypes.ADMIN_MAIN_CATEGORIES_MANAGEMENT_LOG, AdministratorActivityLogs.MAIN_CATEGORY_INSERT_NEW);
		} else {
			return save(loginUser, category, uploadedFiles, LogTypes.ADMIN_MAIN_CATEGORIES_MANAGEMENT_LOG, AdministratorActivityLogs.MAIN_CATEGORY_MODIFY);
		}
	}
	
	private MainCategory save(User loginUser, MainCategory category, List<IsoFile> uploadedFiles, LogTypes logType, String logDetail) throws IOException {
		
		MainCategory original = null;
		
		if (category.getId() == null) {
			category.setCreatedBy(loginUser);
			category.setCreatedOn(Calendar.getInstance().getTime());
		}else {
			original = mainCatRepo.findOne(category.getId().toString());
			category.setUpdatedBy(loginUser);
			category.setUpdatedOn(Calendar.getInstance().getTime());
		}
		
		if (CollectionUtils.isNotEmpty(uploadedFiles)) {
			for (IsoFile uploadFile : uploadedFiles) {
				//insert gridFS File and IsoDocument
				IsoDocument file = new IsoDocument();	
				file.setCreatedBy(loginUser);
				file.setCreatedOn(Calendar.getInstance().getTime());
				
				GridFSFile gridfsFile = isoDocumentRepository.insertGridTemplateFile
						(uploadFile.getInputStream(), uploadFile.getFileName(), uploadFile.getContentType(), loginUser.getOrganization());
				
				file = isoDocumentRepository.insertIsoDocument(file, gridfsFile);
				category.getLstTemplates().add(file);
			}
		}
		category = mainCatRepo.save(category);
		
		//inser activity log
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.DOCUMENT);
		logActivity.setActiviy(logDetail);
		logActivity.setDomainRef(category);
		logActivity.setDomainVersion(new DomainObjectVersion<MainCategory>(MainCategory.class, original, category));
		userActivityService.save(logActivity);
		
		return category;
	}
	
	public void delete(User loginUser, MainCategory category) throws BusinessException {
		category = mainCatRepo.findOne(category.getId().toString());
		
		List<MainCategory> subLst = mainCatRepo.findByParentOrderByNameAsc(category);
		if (!CollectionUtils.isEmpty(subLst)) {
			for (MainCategory sub : subLst) {
				this.delete(loginUser, sub);
			}
		}
		
		if(CollectionUtils.isNotEmpty(category.getLstTemplates())) {
			for(IsoDocument template : category.getLstTemplates()) {
				documentRepository.deleteIsoDocument(template, true);
			}
		}
		
		mainCatRepo.delete(category);
		
		if (category.isRoot()) {
			MainCategoryType mainCategoryType = category.getType();  
			mainCategoryTypeService.save(FaceContextUtils.getLoginUser(), mainCategoryType);
		}
		
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.ADMIN_MAIN_CATEGORIES_MANAGEMENT_LOG);
		logActivity.setActiviy(AdministratorActivityLogs.MAIN_CATEGORY_DELETE);
		logActivity.setDomainVersion(new DomainObjectVersion<MainCategory>(MainCategory.class, category, null));
		logActivity.setDomainRef(category);
		userActivityService.save(logActivity);
	}
	
	public MainCategory getRootCategoryByCategoryType(MainCategoryType categoryType) {
		return mainCatRepo.findByRootAndType(true, categoryType);
	}
	
}
