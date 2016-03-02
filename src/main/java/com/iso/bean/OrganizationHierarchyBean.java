package com.iso.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.extensions.component.gchart.model.GChartModel;
import org.primefaces.extensions.component.gchart.model.GChartModelBuilderCustom;
import org.primefaces.extensions.component.gchart.model.GChartType;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.springframework.dao.DataAccessException;

import com.iso.domain.Organization;
import com.iso.domain.OrganizationUnit;
import com.iso.domain.User;
import com.iso.exception.BusinessException;
import com.iso.service.OrganizationService;
import com.iso.service.OrganizationUnitService;
import com.iso.service.UserService;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="orgHierachyBean")
@ViewScoped
public class OrganizationHierarchyBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GChartModel treeChartModel = null;
	private TreeNode root;
	private TreeNode movetoRootNode;
	private TreeNode selectedNode;
	private TreeNode moveToselectedNode;
	
	private OrganizationUnit selectedOrgUnit;
	private Organization selectedOrg;
	
	private String editOrgUnitDialogHeader;
	private int activeTabIndex;
	
	private boolean isEmptyStructure;
	
	private DualListModel<User> userList;
	
	public OrganizationHierarchyBean(){
	}
	
	@ManagedProperty(value="#{organizationService}")
	private OrganizationService organizationService;	
	
	@ManagedProperty(value="#{organizationUnitService}")
	private OrganizationUnitService organizationUnitService;
	
	@ManagedProperty(value="#{userService}")
	private UserService userService;
	
	public TreeNode getRoot() {
		return root;
	}
	public void setRoot(TreeNode root) {
		this.root = root;
	}
	public TreeNode getSelectedNode() {
		return selectedNode;
	}
	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}
	public TreeNode getMoveToselectedNode() {
		return moveToselectedNode;
	}
	public void setMoveToselectedNode(TreeNode moveToselectedNode) {
		this.moveToselectedNode = moveToselectedNode;
	}
	public OrganizationUnit getSelectedOrgUnit() {
		return selectedOrgUnit;
	}
	public void setSelectedOrgUnit(OrganizationUnit selectedOrgUnit) {
		this.selectedOrgUnit = selectedOrgUnit;
	}
	public String getEditOrgUnitDialogHeader() {
		return editOrgUnitDialogHeader;
	}
	public Organization getSelectedOrg() {
		return selectedOrg;
	}
	public void setSelectedOrg(Organization selectedOrg) {
		this.selectedOrg = selectedOrg;
	}
	public void setEditOrgUnitDialogHeader(String editOrgUnitDialogHeader) {
		this.editOrgUnitDialogHeader = editOrgUnitDialogHeader;
	}
	public OrganizationService getOrganizationService() {
		return organizationService;
	}
	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}
	public OrganizationUnitService getOrganizationUnitService() {
		return organizationUnitService;
	}
	public void setOrganizationUnitService(OrganizationUnitService organizationUnitService) {
		this.organizationUnitService = organizationUnitService;
	}
	public boolean isEmptyStructure() {
		return isEmptyStructure;
	}
	public void setEmptyStructure(boolean isEmptyStructure) {
		this.isEmptyStructure = isEmptyStructure;
	}

	public GChartModel getTreeChartModel() {
		return treeChartModel;
	}
	public void setTreeChartModel(GChartModel treeChartModel) {
		this.treeChartModel = treeChartModel;
	}
	public TreeNode getMovetoRootNode() {
		return movetoRootNode;
	}
	public void setMovetoRootNode(TreeNode movetoRootNode) {
		this.movetoRootNode = movetoRootNode;
	}
	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public DualListModel<User> getUserList() {
		return userList;
	}
	public void setUserList(DualListModel<User> userList) {
		this.userList = userList;
	}
	public int getActiveTabIndex() {
		return activeTabIndex;
	}
	public void setActiveTabIndex(int activeTabIndex) {
		this.activeTabIndex = activeTabIndex;
	}
	@PostConstruct
    public void initialize() {
		selectedOrgUnit = new OrganizationUnit();
		
		Organization organization = FaceContextUtils.getLoginUser().getOrganization();
		this.selectedOrg = organization;
		this.reloadOrganizationStructure();
		
    }
	
	private void reloadOrganizationStructure() {
		
		this.root = getRootOrganizationUnit(this.selectedOrg);
		this.isEmptyStructure = (root.getChildCount() == 0);
		
		treeChartModel = new GChartModelBuilderCustom().setChartType(GChartType.ORGANIZATIONAL)  
                .addColumns("name","description")  
                .importTreeNode(this.root)  
                //.addOption("size", "large")  
                .build();
		
	}
	/**
	 * Recursive method: Create organization unit tree node
	 * @param organization @Organization
	 * @param orgUnitParent @OrganizationUnit
	 * @param parentNode @TreeNode
	 * @return
	 */
	private boolean createOrgUnitTreeNode(Organization organization, OrganizationUnit orgUnitParent, TreeNode parentNode) {
		List<OrganizationUnit> lstOrgUnitChild = organizationUnitService.getOrganizationChildByParent(organization, orgUnitParent);
		if (!CollectionUtils.isEmpty(lstOrgUnitChild)) {
			for (OrganizationUnit orgUnitChild : lstOrgUnitChild) {
				TreeNode childNode = new DefaultTreeNode(orgUnitChild, parentNode);
				childNode.setExpanded(true);
				if (orgUnitChild.getId().equals(this.selectedOrgUnit.getId())) {
					childNode.setSelected(true);
					this.selectedNode = childNode;
				}
				this.createOrgUnitTreeNode(organization, orgUnitChild, childNode);
			}
		}
		return true;
	}
	
	/**
	 * Create root organization, this method will call the recursive method to create whole organization tree
	 * @return @TreeNode
	 */
	private TreeNode getRootOrganizationUnit(Organization organization){
		
		TreeNode hideRootNode = new DefaultTreeNode(new OrganizationUnit(), null);
		hideRootNode.setExpanded(true);
		
		OrganizationUnit rootOrgUnit = organizationUnitService.getOrganizationRoot(organization);
		
		if (rootOrgUnit != null) {
			TreeNode startOrgUnitNode = new DefaultTreeNode(rootOrgUnit, hideRootNode);
			if (rootOrgUnit.getId().equals(this.selectedOrgUnit.getId())) {
				startOrgUnitNode.setSelected(true);
				this.selectedNode = startOrgUnitNode;
			}
			createOrgUnitTreeNode(organization, rootOrgUnit, startOrgUnitNode);
			startOrgUnitNode.setExpanded(true);
		}
		
		return hideRootNode;
	}
	
	/**
	 * Reset the selected org unit to new
	 * @param event
	 */
	public void doAddOrganizationUnit(ActionEvent event){
		this.selectedOrgUnit = new OrganizationUnit();
		this.editOrgUnitDialogHeader = "New Organization Unit";
	}
	
	/**
	 * Set selectedOrgUnit
	 * @param event
	 */
	public void doRenameOrganizationUnit(ActionEvent event) {
		this.selectedOrgUnit = (OrganizationUnit) this.selectedNode.getData();
		this.editOrgUnitDialogHeader = "Rename Organization Unit";
	}
	
	/**
	 * Save org unit (new and edit mode)
	 * @param event
	 */
	public void doSaveOrganizationUnit(ActionEvent event){
		try {
			boolean isNew = (this.selectedOrgUnit.getId() == null);
			if (isNew) {
				if (this.root.getChildCount() == 0) {
					this.selectedOrgUnit.setParent(null);
				}else {
					OrganizationUnit parent = (OrganizationUnit) selectedNode.getData();
					this.selectedOrgUnit.setParent(parent);
				}
			}
			this.selectedOrgUnit.setOrganization(getSelectedOrg());
			this.selectedOrgUnit = organizationUnitService.saveOrganizationUnitData(FaceContextUtils.getLoginUser(), selectedOrgUnit);
			
			reloadDataScreen();
			
			FaceContextUtils.showInfoMessage("New Organization Unit Successfully!", null);
		}catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when create new organization unit!",null);
			
		} catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(),null);
		}
	}
	
	/**
	 * Delete category
	 * @param event
	 */
	public void doDeleteCategory(ActionEvent event) {
		try {
			organizationUnitService.delete(FaceContextUtils.getLoginUser(), this.selectedOrgUnit);	
			this.selectedNode = this.selectedNode.getParent();
			this.selectedOrgUnit = (OrganizationUnit) this.selectedNode.getData();
			reloadDataScreen();
			
			FaceContextUtils.showInfoMessage("Delete Organization Unit Successfully!", null);
		}catch (Exception e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when create new organization unit!",e.getMessage());
			
		}
	}

	/**
	 * Refresh data of selected category
	 * @param event
	 */
	public void onNodeSelect(NodeSelectEvent event) {
		this.selectedNode = event.getTreeNode();
		this.selectedOrgUnit = (OrganizationUnit) this.selectedNode.getData();
		if (this.activeTabIndex == 1) { //assign user to org tab
			this.loadUserListOfOrgUnit(this.selectedOrgUnit);
		}
	}
	
	public void onNodeUnSelect(NodeUnselectEvent event) {
		this.selectedNode = new DefaultTreeNode(null, null);
		this.selectedOrgUnit = new OrganizationUnit();
		this.activeTabIndex = 0;
		
	}
	
	/**
	 * select node in move to org unit dialog
	 * @param event
	 */
	public void onMoveToNodeSelect(NodeSelectEvent event) {
		OrganizationUnit node = (OrganizationUnit) event.getTreeNode().getData();
		if (node.getId().equals(this.selectedOrgUnit.getId())) {
			this.moveToselectedNode = new DefaultTreeNode(null, null);
			event.getTreeNode().setSelected(false);
			
			FaceContextUtils.showErrorMessage("Cannot move the selected organization unit into itself!",null);
		}else {
			this.moveToselectedNode = event.getTreeNode();
		}
	}
	
	/**
	 * unselect node in move to org unit dialog
	 * @param event
	 */
	public void onMoveToNodeUnSelect(NodeUnselectEvent event) {
		this.moveToselectedNode = new DefaultTreeNode(null, null);
	}
	
	/**
	 * Check the category node is selected or not
	 * @return
	 */
	public boolean isSelectCategoryNode() {
		return this.selectedNode != null && this.selectedNode.getData() != null;
	}
	
	public boolean isSelectMoveToCategoryNode() {
		return this.moveToselectedNode != null && this.moveToselectedNode.getData() != null;
	}
	
	/**
	 * Click event in context menu to open move to org unit dialog
	 * @param event
	 */
	public void doOpenMoveToDialog(ActionEvent event) {
		this.movetoRootNode = getRootOrganizationUnit(this.selectedOrg);
		this.moveToselectedNode = new DefaultTreeNode(null, null);
	}
	
	/**
	 * Save event in Move to Org Unit dialog
	 * @param event
	 */
	public void doMoveCategory(ActionEvent event) {
		try {
			OrganizationUnit parent = (OrganizationUnit) moveToselectedNode.getData();
			this.selectedOrgUnit.setParent(parent);
			organizationUnitService.saveOrganizationUnitData(FaceContextUtils.getLoginUser(), selectedOrgUnit);
			
			reloadDataScreen();
			
			FaceContextUtils.showInfoMessage("Move Organization Unit Successfully!", null);
		} catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when create new Organization Unit!",null);
		} catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(),null);
		}
	}
	
	
	public void onTabChange(TabChangeEvent event) {
		this.activeTabIndex = event.getComponent().getChildren().indexOf(event.getTab());
		if (this.activeTabIndex == 1) {
			this.loadUserListOfOrgUnit(this.selectedOrgUnit);
		}
    }
	
	public void reloadUserAssigntList(ActionEvent event) {
		this.loadUserListOfOrgUnit(this.selectedOrgUnit);
	}
	
	public void reloadDataScreen() {
		this.reloadOrganizationStructure();
		//this.getRootOrganizationUnit(this.selectedOrg);
		if (this.activeTabIndex == 1) {
			this.loadUserListOfOrgUnit(this.selectedOrgUnit);
		}
	}
	
	private void loadUserListOfOrgUnit(OrganizationUnit orgUnit) {
		List<User> allUser = userService.getUserLstByOrganization(this.selectedOrg);
		List<User> assignedUser = this.selectedOrgUnit.getUsers();
		assignedUser = CollectionUtils.isEmpty(assignedUser) ? new ArrayList<User>() : assignedUser;
		List<User> sourceUser = filterUserSource(allUser, assignedUser);
		this.setUserList(new DualListModel<User>(sourceUser, assignedUser));
	}

	private List<User> filterUserSource(List<User> allUserLst, List<User> targetUserLst) {
		List<User> sourceUserLst = new ArrayList<User>();
		sourceUserLst.addAll(allUserLst);
		
		if (!CollectionUtils.isEmpty(allUserLst)) {
			if (CollectionUtils.isEmpty(targetUserLst)) {
				return sourceUserLst;
			} else {
				for(User user : allUserLst) {
					for (User assignedUser : targetUserLst) {
						if (user != null && user.getId() != null && assignedUser != null && user.getId().equals(assignedUser.getId())) {
							sourceUserLst.remove(user);
						}
					}
				}
			}
		}
		return sourceUserLst;
	}
	
	public void doSaveUserAssignment(ActionEvent event) {
		try{
			List<User> originalUserLst = organizationUnitService.getOrganizationUnitById(this.selectedOrgUnit.getId().toString()).getUsers();
			originalUserLst = CollectionUtils.isEmpty(originalUserLst) ? new ArrayList<User>() : originalUserLst;
			saveUserAssignToOrgUnit(originalUserLst, this.userList.getTarget());

			this.selectedOrgUnit.setUsers(this.userList.getTarget());
			organizationUnitService.saveUserAssignedToOrgUnit(FaceContextUtils.getLoginUser(), this.selectedOrgUnit);
			
			FaceContextUtils.showInfoMessage("Save Successfully!", null);
		}catch(DataAccessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when save assigned users!",null);
		}catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(),null);
		}
	}
	
	private void saveUserAssignToOrgUnit(List<User> original, List<User> target) {
		List<User> removed = new ArrayList<User>(original);
		List<User> added = new ArrayList<User>(target);
		for (User oldUser : original) {
			for (User newUser : target) {
				if (oldUser.getId().equals(newUser.getId())) {
					removed.remove(oldUser);
					added.remove(newUser);
				}
			}
		}
		
		for (User remove : removed) {
			if (CollectionUtils.isNotEmpty(remove.getOrganizationUnits())) {
				for (OrganizationUnit unit : remove.getOrganizationUnits()) {
					if (this.selectedOrgUnit.getId().equals(unit.getId())) {
						remove.getOrganizationUnits().remove(unit);
						break;
					}
				}
				try {
					userService.saveOrgUnitAssignment(remove, FaceContextUtils.getLoginUser());
				} catch (BusinessException e) {
					e.printStackTrace();
					FaceContextUtils.showErrorMessage(e.getMessage(), null);
				}
			}
		}
		
		for (User add : added) {
			if (CollectionUtils.isEmpty(add.getOrganizationUnits())) {
				add.setOrganizationUnits(new ArrayList<OrganizationUnit>());
			}
			add.getOrganizationUnits().add(this.selectedOrgUnit);
			try {
				userService.saveOrgUnitAssignment(add, FaceContextUtils.getLoginUser());
			} catch (BusinessException e) {
				e.printStackTrace();
				FaceContextUtils.showErrorMessage(e.getMessage(), null);
			}
		}
	}
}
