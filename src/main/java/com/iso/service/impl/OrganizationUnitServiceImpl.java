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
import com.iso.repository.OrganizationRepository;
import com.iso.repository.OrganizationUnitRepository;
import com.iso.repository.UserRepository;
import com.iso.service.CategoryService;
import com.iso.service.IsoDocumentService;
import com.iso.service.OrganizationUnitService;
import com.iso.service.UserActivityService;
import com.iso.service.UserService;

@Component(value="organizationUnitService")
public class OrganizationUnitServiceImpl implements OrganizationUnitService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	private OrganizationUnitRepository organizationUnitRepository;
	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	@Qualifier(value="userService")
	private UserService userService;
	
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
	
	public List<OrganizationUnit> getOrganizationChildByParent(Organization organization, OrganizationUnit parent) {		
		List<OrganizationUnit> orgUnitLst = organizationUnitRepository.findByOrganizationAndParentOrderByNameAsc(organization, parent);
		return orgUnitLst;
	}
	
	public OrganizationUnit getOrganizationRoot(Organization organization) {
		List<OrganizationUnit> orgUnitLst = organizationUnitRepository.findByOrganizationAndParentOrderByNameAsc(organization, null);
		if (!CollectionUtils.isEmpty(orgUnitLst) && orgUnitLst.size() > 0) {
			return orgUnitLst.get(0);
		}
		return null;
	}
	
	public OrganizationUnit saveOrganizationUnitData(User loginUser, OrganizationUnit orgUnit) throws BusinessException {
		if (orgUnit.getId() == null) {
			return save(loginUser, orgUnit, LogTypes.ADMIN_ORGANIZATION_UNIT_MANAGEMENT_LOG, AdministratorActivityLogs.ORGANIZATION_UNIT_INSERT_NEW);
		}else {
			return save(loginUser, orgUnit, LogTypes.ADMIN_ORGANIZATION_UNIT_MANAGEMENT_LOG, AdministratorActivityLogs.ORGANIZATION_UNIT_MODIFY);
		}
	}
	
	public OrganizationUnit saveUserAssignedToOrgUnit(User loginUser, OrganizationUnit orgUnit) throws BusinessException {
		orgUnit = save(loginUser, orgUnit, LogTypes.ADMIN_ORGANIZATION_UNIT_MANAGEMENT_LOG, AdministratorActivityLogs.ORGANIZATION_UNIT_ASSIGN_TO_USER);
		
		return orgUnit;
	}
	
	
	private OrganizationUnit save(User user, OrganizationUnit orgUnit, LogTypes logType, String logDetail) throws BusinessException {
		OrganizationUnit original = null;
		UserActivity logActivity = new UserActivity(user, logType);
		logActivity.setActiviy(logDetail);
		
		if(orgUnit.getId() == null) {
			if(organizationUnitRepository.findByCodeAndOrganization(orgUnit.getCode(), orgUnit.getOrganization()) != null) {
				throw new BusinessException(Messages.DUPLICATE_ORGANIZATION_UNIT_CODE);
			}
		}else {
			original = organizationUnitRepository.findOne(orgUnit.getId().toString());
		}
		orgUnit = organizationUnitRepository.save(orgUnit);
		
		logActivity.setDomainVersion(new DomainObjectVersion<OrganizationUnit>(OrganizationUnit.class, original, orgUnit));
		userActivityService.save(logActivity);
		
		return orgUnit;
	}
	
	public void delete(User loginUser, OrganizationUnit orgUnit) throws BusinessException {
		
		//Delete sub organization unit
		List<OrganizationUnit> subLst = organizationUnitRepository.findByParentOrderByIdAsc(orgUnit);
		if (!CollectionUtils.isEmpty(subLst)) {
			for (OrganizationUnit sub : subLst) {
				this.delete(loginUser, sub);
			}
		}
		
		//Delete uses assigned to this org unit
		List<User> assignedUsers = userRepository.findUserAssignedToOrgUnit(loginUser.getOrganization().getId().toString(), orgUnit.getId().toString());
		if (CollectionUtils.isNotEmpty(assignedUsers)) {
			for (User assignedUser : assignedUsers) {
				for(OrganizationUnit assignedUnit: assignedUser.getOrganizationUnits()) {
					if(assignedUnit.getId().equals(orgUnit.getId())) {
						assignedUser.getOrganizationUnits().remove(assignedUnit);
						break;
					}
				}
				userService.saveOrgUnitAssignment(assignedUser, loginUser);
			}
		}
		
		//Delete category security assigned to this org unit
		List<Category> assignedCategorys = categoryRepository.findByCategorySecurityOrganizationUnit(orgUnit.getId().toString(), loginUser.getOrganization().getId().toString());
		if (CollectionUtils.isNotEmpty(assignedCategorys)) {
			for (Category assignedCategory : assignedCategorys) {
				for(CategorySecurity assignedSecurity: assignedCategory.getCategorySecurities()) {
					if(assignedSecurity.getOrgUnit() != null && orgUnit.getId().equals(assignedSecurity.getOrgUnit().getId())) {
						assignedCategory.getCategorySecurities().remove(assignedSecurity);
						break;
					}
				}
				categoryService.saveCategorySecurity(loginUser, assignedCategory);
			}
		}
		
		//Delete document security assigned to this org unit
		List<IsoDocument> assignedDocuments = isoDocumentRepository.findByDocumentSecurityOrganizationUnit(orgUnit.getId().toString(), loginUser.getOrganization().getId().toString()); 
		if (CollectionUtils.isNotEmpty(assignedDocuments)) {
			for (IsoDocument assignedDocument : assignedDocuments) {
				for(DocumentSecurity assignedSecurity: assignedDocument.getDocumentSecurities()) {
					if(assignedSecurity.getOrgUnit() != null && orgUnit.getId().equals(assignedSecurity.getOrgUnit().getId())) {
						assignedDocument.getDocumentSecurities().remove(assignedSecurity);
						break;
					}
				}
				isoDocumentService.saveDocumentSecurity(loginUser, assignedDocument);
			}
		}
		
		
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.ADMIN_ORGANIZATION_UNIT_MANAGEMENT_LOG);
		logActivity.setActiviy(AdministratorActivityLogs.ORGANIZATION_UNIT_DELETE);
		logActivity.setDomainVersion(new DomainObjectVersion<OrganizationUnit>(OrganizationUnit.class, orgUnit, null));
		
		organizationUnitRepository.delete(orgUnit);
		userActivityService.save(logActivity);
	}
	
	/*@SuppressWarnings("unchecked")
	public List<OrganizationUnit> getAllOrganizationUnit(Organization organization) {
		return IteratorUtils.toList(organizationUnitRepository.findAll(new Sort(Sort.Direction.ASC, "name")).iterator());
	}*/
	
	public List<OrganizationUnit> searchByName(Organization organization, String name) {
		String searchText = StringEscapeUtils.escapeSql(StringUtils.trimToEmpty(name).replace("(", "").replace(")", ""));
		return organizationUnitRepository.findByOrganizationAndNameLikeIgnoreCaseOrderByNameAsc(organization, searchText);
	}
	
	public OrganizationUnit getOrganizationUnitById(String id) {
		return organizationUnitRepository.findOne(id);
	}
}
