package com.iso.util;

import org.apache.commons.collections.CollectionUtils;

import com.iso.domain.Menu;
import com.iso.domain.User;
import com.iso.domain.UserRole;
import com.iso.jaxb.menu.GroupMenu;
import com.iso.jaxb.menu.MenuItem;

public class UserRoleChecker {

	public static boolean isMenuAccessPermit(User user, MenuItem menuItem) {
		if (CollectionUtils.isNotEmpty(user.getUserRoles())) {
			for(UserRole role : user.getUserRoles()) {
				if (CollectionUtils.isNotEmpty(role.getMenuLst())) {
					for(Menu menu : role.getMenuLst()) {
						if (menu.getCode().equalsIgnoreCase(menuItem.getCode())) {
							return true;
						} else {
							boolean result = false;
							if (CollectionUtils.isNotEmpty(menuItem.getGroups())) {
								for (GroupMenu group : menuItem.getGroups()) {
									if (CollectionUtils.isNotEmpty(group.getMenuItems())) {
										for (MenuItem subMenu : group.getMenuItems()) {
											result = isMenuAccessPermit(user, subMenu);
											if (result) {
												return result;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
}
