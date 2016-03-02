package com.iso.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.iso.constant.AdministratorActivityLogs;
import com.iso.constant.LogTypes;
import com.iso.constant.Messages;
import com.iso.domain.Category;
import com.iso.domain.CategorySecurity;
import com.iso.domain.DocumentSecurity;
import com.iso.domain.IsoDocument;
import com.iso.domain.Organization;
import com.iso.domain.OrganizationUnit;
import com.iso.domain.User;
import com.iso.domain.UserActivity;
import com.iso.domain.generic.DomainObjectVersion;
import com.iso.exception.BusinessException;
import com.iso.repository.CategoryRepository;
import com.iso.repository.IsoDocumentRepository;
import com.iso.repository.OrganizationUnitRepository;
import com.iso.repository.UserRepository;
import com.iso.service.CategoryService;
import com.iso.service.IsoDocumentService;
import com.iso.service.OrganizationUnitService;
import com.iso.service.UserActivityService;
import com.iso.service.UserService;

@Component(value="userService")
public class UserServiceImpl implements UserService{

	private static final long serialVersionUID = 1L;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrganizationUnitRepository organizationUnitRepository;
	@Autowired
	@Qualifier(value="organizationUnitService")
	private OrganizationUnitService organizationUnitService;
	
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	@Qualifier(value="categoryService")
	private CategoryService categoryService;
	
	@Autowired
	private IsoDocumentRepository isoDocumentRepository;
	@Autowired
	@Qualifier(value="isoDocumentService")
	private IsoDocumentService isoDocumentService;
	
	@Autowired
	@Qualifier(value="userActivityService")
	private UserActivityService userActivityService;
	
	public User saveOrgUnitAssignment(User user, User loginUser) throws BusinessException {
		return save(user, loginUser, LogTypes.ADMIN_ORGANIZATION_UNIT_MANAGEMENT_LOG, AdministratorActivityLogs.ORGANIZATION_UNIT_ASSIGN_TO_USER);
	}
	
	public User saveUserProfile(User user, User loginUser) throws BusinessException{
		if(user.getId() == null) {
			return save(user, loginUser, LogTypes.ADMIN_USER_MANAGEMENT_LOG, AdministratorActivityLogs.USER_INSERT_NEW);
		}else {
			return save(user, loginUser, LogTypes.ADMIN_USER_MANAGEMENT_LOG, AdministratorActivityLogs.USER_MODIFY);
		}
	}
	
	public User saveUserRoleAssignment(User user, User loginUser) throws BusinessException{
		return save(user, loginUser, LogTypes.ADMIN_USER_MANAGEMENT_LOG, AdministratorActivityLogs.USER_ROLE_ASSIGN_TO_USER);
	}
	
	private User save(User user, User loginUser, LogTypes logType, String logDetail) throws BusinessException {
		
		User originalUser = null;
		UserActivity logActivity = new UserActivity(loginUser, logType);
		logActivity.setActiviy(logDetail);
		
		User existingUser = userRepository.findByUsernameAndOrganization(user.getUsername(), user.getOrganization());
		if (existingUser != null) {
			if (user.getId() == null || !existingUser.getId().equals(user.getId())) {
				throw new BusinessException(Messages.DUPLICATE_USERNAME);
			}
		}
		
		if (user.getId() != null) {
			originalUser = userRepository.findOne(user.getId().toString());
		}
		
		user = userRepository.save(user);
		logActivity.setDomainVersion(new DomainObjectVersion<User>(User.class, originalUser, user));
		userActivityService.save(logActivity);
		
		return user;
	}
	
	public void delete(String userId, User loginUser) throws BusinessException {
		
		User user = userRepository.findOne(userId);
		
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.ADMIN_USER_MANAGEMENT_LOG);
		logActivity.setActiviy(AdministratorActivityLogs.USER_DELETE);
		logActivity.setDomainVersion(new DomainObjectVersion<User>(User.class, user, null));
		
		//Delete org unit assigned to this user
		List<OrganizationUnit> assignedOrgUnits = organizationUnitRepository.findByAssignedUser(userId);
		
		if (CollectionUtils.isNotEmpty(assignedOrgUnits)) {
			for (OrganizationUnit assignedOrgUnit : assignedOrgUnits) {
				for(User assignedUser: assignedOrgUnit.getUsers()) {
					if(assignedUser.getId().equals(user.getId())) {
						assignedOrgUnit.getUsers().remove(assignedUser);
						break;
					}
				}
				organizationUnitService.saveUserAssignedToOrgUnit(loginUser, assignedOrgUnit);
			}
		}
		
		//Delete category security assigned to this user
		List<Category> assignedCategorys = categoryRepository.findByCategorySecurityUser(userId, loginUser.getOrganization().getId().toString());
		if (CollectionUtils.isNotEmpty(assignedCategorys)) {
			for (Category assignedCategory : assignedCategorys) {
				for(CategorySecurity assignedSecurity: assignedCategory.getCategorySecurities()) {
					if(assignedSecurity.getUser() != null && assignedSecurity.getUser() != null && user.getId().equals(assignedSecurity.getUser().getId())) {
						assignedCategory.getCategorySecurities().remove(assignedSecurity);
						break;
					}
				}
				categoryService.saveCategorySecurity(loginUser, assignedCategory);
			}
		}
		
		//Delete document security assigned to this user
		List<IsoDocument> assignedDocuments = isoDocumentRepository.findByDocumentSecurityUser(userId, loginUser.getOrganization().getId().toString()); 
		if (CollectionUtils.isNotEmpty(assignedDocuments)) {
			for (IsoDocument assignedDocument : assignedDocuments) {
				for(DocumentSecurity assignedSecurity: assignedDocument.getDocumentSecurities()) {
					if(assignedSecurity.getUser() != null && assignedSecurity.getUser() != null && user.getId().equals(assignedSecurity.getUser().getId())) {
						assignedDocument.getDocumentSecurities().remove(assignedSecurity);
						break;
					}
				}
				isoDocumentService.saveDocumentSecurity(loginUser, assignedDocument);
			}
		}
		
		userRepository.delete(user);
		userActivityService.save(logActivity);
	}
	
	public List<User> getUserLstByOrganization(Organization organization){
		return userRepository.findByOrganizationOrderByIdAsc(organization);
	}	
	
	public User getUserById(String id){
		return userRepository.findOne(id);
	}
	
	public User getUserByUserName(String username) {
		return userRepository.findByUsername(username);
	}
	
	public User getUserByUserNameAndOrganization(String username, Organization organization) {
		return userRepository.findByUsername(username);
	}
	
	public List<User> getUserLstByOrganizationAndOrganizationUnit(Organization org, OrganizationUnit orgUnit) {
		return userRepository.findByOrganizationAndOrganizationUnitsOrderByIdAsc(org, orgUnit);
	}
	
	public List<User> searchByName(Organization organization, String name) {
		String searchText = StringEscapeUtils.escapeSql(StringUtils.trimToEmpty(name).replace("(", "").replace(")", ""));
		return userRepository.searchLikeName(organization, searchText);
	}
	
}
