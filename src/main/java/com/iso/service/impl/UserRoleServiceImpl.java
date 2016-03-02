package com.iso.service.impl;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.iso.constant.AdministratorActivityLogs;
import com.iso.constant.LogTypes;
import com.iso.constant.Messages;
import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.domain.UserActivity;
import com.iso.domain.UserRole;
import com.iso.domain.generic.DomainObjectVersion;
import com.iso.exception.BusinessException;
import com.iso.repository.UserRepository;
import com.iso.repository.UserRoleRepository;
import com.iso.service.UserActivityService;
import com.iso.service.UserRoleService;
import com.iso.service.UserService;

@Component(value="userRoleService")
public class UserRoleServiceImpl implements UserRoleService{	
	
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	
	@Autowired
	@Qualifier(value="userActivityService")
	private UserActivityService userActivityService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	@Qualifier(value="userService")
	private UserService userService;
	
	public UserRole saveUserRoleData(User loginUser, UserRole userRole) throws BusinessException{
		if (userRole.getId() == null) {
			return save(userRole, loginUser, LogTypes.ADMIN_USER_ROLE_MANAGEMENT_LOG, AdministratorActivityLogs.USER_ROLE_INSERT_NEW);
		}else {
			return save(userRole, loginUser, LogTypes.ADMIN_USER_ROLE_MANAGEMENT_LOG, AdministratorActivityLogs.USER_ROLE_MODIFY);
		}
	}
	
	public UserRole saveMenuSecurityAssignedToRole(User loginUser, UserRole userRole) throws BusinessException {
		return save(userRole, loginUser, LogTypes.ADMIN_MENU_SECURITY_MANAGEMENT_LOG, AdministratorActivityLogs.MENU_SECURITY_ASSIGNMENT);
	}
	
	private UserRole save(UserRole userRole, User user, LogTypes logType, String logDetail) throws BusinessException {
		
		UserRole original = null;
		UserActivity logActivity = new UserActivity(user, logType);
		logActivity.setActiviy(logDetail);
		
		UserRole existing = getUserRoleByCodeAndOrganization(userRole.getCode(), userRole.getOrganization());
		if(existing != null && (userRole.getId() == null || !existing.getId().equals(userRole.getId())) ) {
			throw new BusinessException(Messages.DUPLICATE_USER_ROLE_CODE);
		}
		
		if(userRole.getId() == null) {
			userRole.setCreatedBy(user);
			userRole.setCreatedOn(Calendar.getInstance().getTime());
		} else {
			original = userRoleRepository.findOne(userRole.getId().toString());
			userRole.setUpdatedBy(user);
			userRole.setUpdatedOn(Calendar.getInstance().getTime());
		}
		
		userRole = userRoleRepository.save(userRole);
		
		logActivity.setDomainVersion(new DomainObjectVersion<UserRole>(UserRole.class, original, userRole));
		userActivityService.save(logActivity);
		
		return userRole;
	}
	
	public void delete(User loginUser, UserRole userRole) throws BusinessException {
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.ADMIN_USER_ROLE_MANAGEMENT_LOG);
		logActivity.setActiviy(AdministratorActivityLogs.USER_ROLE_DELETE);
		logActivity.setDomainVersion(new DomainObjectVersion<UserRole>(UserRole.class, userRole, null));
		
		List<User> assignedUsers = userRepository.findUserAssignedToUserRole(loginUser.getOrganization().getId().toString(), userRole.getId().toString());
		if (CollectionUtils.isNotEmpty(assignedUsers)) {
			for (User user : assignedUsers) {
				for (UserRole role : user.getUserRoles()) {
					if (role.getId().equals(userRole.getId())) {
						user.getUserRoles().remove(role);
						break;
					}
				}
				userService.saveUserRoleAssignment(user, loginUser);
			}
		}
		
		userRoleRepository.delete(userRole);
		userActivityService.save(logActivity);
	}
	
	public List<UserRole> getUserRoleByOrganization(Organization organization) {
		return userRoleRepository.findByOrganization(organization);
	}
	
	public UserRole getUserRoleById(String id) {
		return userRoleRepository.findOne(id);
	}
	
	public UserRole getUserRoleByCodeAndOrganization(String code, Organization organization) {
		return userRoleRepository.findByCodeAndOrganization(code, organization);
	}
}
