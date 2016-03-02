package com.iso.service.impl;

import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.iso.constant.AdministratorActivityLogs;
import com.iso.constant.LogTypes;
import com.iso.constant.Messages;
import com.iso.domain.Category;
import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.domain.UserActivity;
import com.iso.domain.generic.DomainObjectVersion;
import com.iso.exception.BusinessException;
import com.iso.repository.OrganizationRepository;
import com.iso.service.CategoryService;
import com.iso.service.OrganizationService;
import com.iso.service.UserActivityService;
import com.iso.service.UserRoleService;
import com.iso.service.UserService;

@Component(value="organizationService")
public class OrganizationServiceImpl implements OrganizationService{	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	@Qualifier(value="categoryService")
	private CategoryService categoryService;
	
	@Autowired
	@Qualifier(value="userActivityService")
	private UserActivityService userActivityService;
	
	@Autowired
	@Qualifier(value="userService")
	private UserService userService;
	
	@Autowired
	@Qualifier(value="userRoleService")
	private UserRoleService userRoleService;
	
	
	public Organization saveOrganizationData(User loginUser, Organization org) throws BusinessException {
		if(org.getId() == null) {
			return save(loginUser, org, LogTypes.ADMIN_ORGANIZATION_MANAGEMENT_LOG, AdministratorActivityLogs.ORGANIZATION_INSERT_NEW);
		}else {
			return save(loginUser, org, LogTypes.ADMIN_ORGANIZATION_MANAGEMENT_LOG, AdministratorActivityLogs.ORGANIZATION_MODIFY);
		}
	}
	
	public Organization saveOrganizationData(User loginUser, Organization org, String logDetail) throws BusinessException {
		return save(loginUser, org, LogTypes.ADMIN_ORGANIZATION_MANAGEMENT_LOG, logDetail);
	}
	
	/*private Organization insert(User loginUser, Organization org) throws BusinessException, IOException, JAXBException {
		org = save(loginUser, org, LogTypes.ADMIN_ORGANIZATION_MANAGEMENT_LOG, AdministratorActivityLogs.ORGANIZATION_INSERT_NEW);
		
		//insert organization admin role + menu security
		UserRole orgAdminRole = new UserRole();
		orgAdminRole.setCode(ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.ORG_ADMIN_ROLE_CODE));
		orgAdminRole.setName(ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.ORG_ADMIN_ROLE_NAME));
		orgAdminRole.setMenuLst(DefaultMainMenuSupporter.buildMenu(DefaultMainMenuSupporter.generateMainMenu(), false));
		userRoleService.saveUserRoleData(loginUser, orgAdminRole);
		
		//insert organization admin user
		
	}*/
	
	private Organization save(User user, Organization org, LogTypes logType, String logDetail) throws BusinessException {
		Organization original = null;
		UserActivity logActivity = new UserActivity(user, logType);
		logActivity.setActiviy(logDetail);
		
		if (org.getId() == null) {
			if(organizationRepository.findByCode(org.getCode()) != null) {
				throw new BusinessException(Messages.DUPLICATE_ORGANIZATION_CODE);
			}
		}else {
			original = organizationRepository.findOne(org.getId().toString());
		}
		
		org = organizationRepository.save(org);
		
		logActivity.setDomainVersion(new DomainObjectVersion<Organization>(Organization.class, original, org));
		userActivityService.save(logActivity);
		
		return org;
	}
	
	public void delete(User loginUser,Organization org) throws BusinessException {
		Category rootCategory = categoryService.getRootCategoryByOrganization(org.getId().toString());
		if (rootCategory != null) {
			categoryService.delete(loginUser, rootCategory, true);
		}
		organizationRepository.delete(org);
		
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.ADMIN_ORGANIZATION_MANAGEMENT_LOG);
		logActivity.setActiviy(AdministratorActivityLogs.ORGANIZATION_DELETE);
		logActivity.setDomainVersion(new DomainObjectVersion<Organization>(Organization.class, org, null));
		userActivityService.save(logActivity);
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Organization> getAllOrganization() {
		return IteratorUtils.toList(organizationRepository.findAll().iterator());
	}
	
	public List<Organization> getActiveOrganization(){
		return organizationRepository.findByActiveOrderByNameAsc(true);
	}
	
	public List<Organization> searchOrganozationByName(String name) {
		return organizationRepository.findByNameLikeIgnoreCaseOrderByNameAsc(name);
	}
	
	public Organization getOrganizationById(String id) {
		return organizationRepository.findOne(id);
	}
	
	public Organization getOrganizationByCode(String code) {
		return organizationRepository.findByCode(code);
	}
}
