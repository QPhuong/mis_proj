package com.iso.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.xml.bind.JAXBException;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.dao.DataAccessException;

import com.iso.domain.Menu;
import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.domain.UserRole;
import com.iso.exception.BusinessException;
import com.iso.face.converter.UserRoleConverter;
import com.iso.jaxb.menu.DefaultMainMenuReader;
import com.iso.jaxb.menu.GroupMenu;
import com.iso.jaxb.menu.MainMenu;
import com.iso.jaxb.menu.MenuItem;
import com.iso.service.OrganizationService;
import com.iso.service.UserRoleService;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="menuSecurityBean")
@ViewScoped
public class MenuSecurityBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private List<Organization> organizationLst;
	private Organization selectedOrganization;
	private boolean isMultiOrganization;
	
	private List<UserRole> lstUserRole;
	private TreeNode[] selectedNode;
	
	private UserRole selectedUserRole;
	private UserRole previousUserRole;
	
	private TreeNode root;
	private MainMenu mainmenu;
	
	@ManagedProperty(value="#{userRoleService}")
	private UserRoleService userRoleService;
	
	@ManagedProperty(value="#{organizationService}")
	private OrganizationService organizationService;
	
	public List<UserRole> getLstUserRole() {
		return lstUserRole;
	}
	public void setLstUserRole(List<UserRole> lstUserRole) {
		this.lstUserRole = lstUserRole;
	}
	public TreeNode getRoot() {
		return root;
	}
	public void setRoot(TreeNode root) {
		this.root = root;
	}
	public UserRole getSelectedUserRole() {
		return selectedUserRole;
	}
	public void setSelectedUserRole(UserRole selectedUserRole) {
		this.selectedUserRole = selectedUserRole;
	}
	public TreeNode[] getSelectedNode() {
		return selectedNode;
	}
	public void setSelectedNode(TreeNode[] selectedNode) {
		this.selectedNode = selectedNode;
	}
	public UserRoleService getUserRoleService() {
		return userRoleService;
	}
	public void setUserRoleService(UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}
	public List<Organization> getOrganizationLst() {
		return organizationLst;
	}
	public void setOrganizationLst(List<Organization> organizationLst) {
		this.organizationLst = organizationLst;
	}
	public Organization getSelectedOrganization() {
		return selectedOrganization;
	}
	public void setSelectedOrganization(Organization selectedOrganization) {
		this.selectedOrganization = selectedOrganization;
	}
	public OrganizationService getOrganizationService() {
		return organizationService;
	}
	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}
	public boolean isMultiOrganization() {
		return isMultiOrganization;
	}
	public void setMultiOrganization(boolean isMultiOrganization) {
		this.isMultiOrganization = isMultiOrganization;
	}
	
	
	@PostConstruct
	private void initialize() {
		try {
			User loginUser = FaceContextUtils.getLoginUser();
			Object bean = FaceContextUtils.getApplicationScopeBean("applicationSetupBean");
			if (bean != null) {
				ApplicationSetupBean appSetupBean = (ApplicationSetupBean) bean;
			    isMultiOrganization = appSetupBean.isMultiOrganizationSetup();
			}
			
			//only show multi organization selection for system admin and with multi organization application setup
			isMultiOrganization = isMultiOrganization && loginUser.isSysAdministrator();
			
			if(!isMultiOrganization) {
				this.selectedOrganization = loginUser.getOrganization();
			}else {
				this.organizationLst = organizationService.getActiveOrganization();
				this.selectedOrganization = (CollectionUtils.isNotEmpty(this.organizationLst) ? this.organizationLst.get(0) : null);
			}
			
			this.mainmenu = DefaultMainMenuReader.generateMainMenu();
			this.root = new DefaultTreeNode(new MenuItem(), null);
			doLoadUserRoleByOrganization();
			
		} catch (JAXBException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when read menu information", e.getStackTrace().toString());
		} catch (IOException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when read menu information", e.getStackTrace().toString());
		}
	}
	
	public void doLoadUserRoleByOrganization() {
		this.selectedUserRole = new UserRole();
		this.lstUserRole = userRoleService.getUserRoleByOrganization(this.selectedOrganization);
		this.selectedNode = null;
		this.root = new DefaultTreeNode(new MenuItem(), null);
		
		Object converter = FaceContextUtils.getViewScopeBean("roleConverter");
		if (converter != null) {
			UserRoleConverter bean = (UserRoleConverter) converter;
			bean.getLstUserRole().clear();
			bean.getLstUserRole().addAll(lstUserRole);
		}
	}
	
	private TreeNode buildMenuItemTreeNode(MenuItem menuItem, TreeNode parentNode, List<Menu> selectedMenu) {
		
		Menu menu = new Menu(menuItem.getCode(), menuItem.getName(), false);
		TreeNode menuNode = new DefaultTreeNode(menu, parentNode);
		menuNode.setExpanded(true);
		
		if(CollectionUtils.isNotEmpty(menuItem.getGroups())) {
			for (GroupMenu group : menuItem.getGroups()) {
				menu = new Menu(group.getName(), group.getName(), true);
				TreeNode groupNode = new DefaultTreeNode(menu, menuNode);
				groupNode.setExpanded(true);
				boolean shouldSelectGroup = true;
				
				if(CollectionUtils.isNotEmpty(group.getMenuItems())) {
					for(MenuItem item : group.getMenuItems()) {
						if (!item.isSecurity() || 
							(this.selectedOrganization.isOwnerOrganization() && item.isSecurity() && this.selectedUserRole.isSystemAdminRole())) {
							TreeNode childNode = buildMenuItemTreeNode(item, groupNode, selectedMenu);
							shouldSelectGroup = shouldSelectGroup && childNode.isSelected();
						}
					}
				}
				groupNode.setSelected(shouldSelectGroup);
			}
		}
		if(checkToSelectMenuNode(menuNode, selectedMenu)) {
			menuNode.setSelected(true);
		}
		return menuNode;
	}
	
	private void buildRootMenuTreeNode(List<Menu> selectedMenu) {
		this.root = new DefaultTreeNode(new MenuItem(), null);
		this.root.setExpanded(true);
		if(mainmenu != null && CollectionUtils.isNotEmpty(mainmenu.getMainMenuItems())) {
			for(MenuItem menuItem : mainmenu.getMainMenuItems()) {
				buildMenuItemTreeNode(menuItem, this.root, selectedMenu);
			}
		}
	}
	
	public boolean checkToSelectMenuNode(TreeNode menuNode, List<Menu> menuLst) {
		Menu menu = (Menu) menuNode.getData();
		if (CollectionUtils.isNotEmpty(menuLst)) {
			for(Menu existing : menuLst) {
				if (existing.getCode().equalsIgnoreCase(menu.getCode())) {
					menuLst.remove(existing);
					menuNode.setSelected(true);
					return true;
				}
			}
		}
		return false;
	}
	public void doSelectUserRole() {
		
		if (this.previousUserRole == null) {
			this.previousUserRole = this.selectedUserRole;
		} else {
			List<Menu> lstSelectedMenu = new ArrayList<Menu>();
			for(TreeNode node: this.selectedNode) {
				Menu menu = (Menu)node.getData();
				if(!menu.isGroup()) {
					lstSelectedMenu.add((Menu)node.getData());
				}
			}
			this.previousUserRole.setMenuLst(lstSelectedMenu);
		}
		this.selectedNode = new TreeNode[0];
		buildRootMenuTreeNode(this.selectedUserRole.getMenuLst());
		//this.selectedNode = this.selectedNodeLst.toArray(new TreeNode[this.selectedNodeLst.size()]);
		this.previousUserRole = this.selectedUserRole;
	}
	
	public void doSaveAll() {
		try {
			if (this.selectedUserRole != null) {
				List<Menu> lstSelectedMenu = new ArrayList<Menu>();
				for(TreeNode node: this.selectedNode) {
					lstSelectedMenu.add((Menu)node.getData());
				}
				this.selectedUserRole.setMenuLst(lstSelectedMenu);
			}
			if (CollectionUtils.isNotEmpty(this.lstUserRole)) {
				for (UserRole role : this.lstUserRole) {
					userRoleService.saveMenuSecurityAssignedToRole(FaceContextUtils.getLoginUser(), role);
				}	
			}
			FaceContextUtils.showInfoMessage("Save menu security successfully", "");
		} catch (DataAccessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when read menu information", e.getStackTrace().toString());
		} catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), null);
		}
	}
	
	public void doResetAll(){
		
		this.lstUserRole.clear();
		this.lstUserRole = userRoleService.getUserRoleByOrganization(this.selectedOrganization);
		
		Object converter = FaceContextUtils.getViewScopeBean("roleConverter");
		if (converter != null) {
			UserRoleConverter bean = (UserRoleConverter) converter;
			bean.getLstUserRole().clear();
			bean.getLstUserRole().addAll(lstUserRole);
		}
		if (CollectionUtils.isNotEmpty(this.lstUserRole) && this.selectedUserRole != null) {
			for (UserRole role : this.lstUserRole) {
				if (role.getId().equals(this.selectedUserRole.getId())){
					this.selectedUserRole = role;
					buildRootMenuTreeNode(this.selectedUserRole.getMenuLst());
				}
			}	
		}
	}
}
