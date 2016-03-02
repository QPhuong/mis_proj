package com.iso.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.dao.DataAccessException;

import com.iso.constant.CategorySortValue;
import com.iso.constant.SortType;
import com.iso.domain.Category;
import com.iso.domain.CategorySecurity;
import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.domain.generic.Property;
import com.iso.exception.BusinessException;
import com.iso.service.CategoryService;
import com.iso.service.OrganizationService;
import com.iso.util.DomainSecurityChecker;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="categoryTreeBean")
@ViewScoped
public class CategoryTreeBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TreeNode root;
	private TreeNode movetoRootNode;
	private TreeNode selectedNode;
	private TreeNode moveToselectedNode;
	private Category selectedCategory;
	
	private String editCategoryDialogHeader;
	
	private Property<CategorySortValue> selectedSortValue;
	private Property<SortType> selectedSortType;
	private List<Property<CategorySortValue>> sortCategoryValues;
	private List<Property<SortType>> sortTypes;
	
	@ManagedProperty(value="#{categoryService}")
	private CategoryService categoryService;
	
	@ManagedProperty(value="#{organizationService}")
	private OrganizationService organizationService;
	
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
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	public void setSelectedCategory(Category selectedCategory) {
		this.selectedCategory = selectedCategory;
	}
	public Category getSelectedCategory(){
		return this.selectedCategory;
	}
	public String getEditCategoryDialogHeader() {
		return editCategoryDialogHeader;
	}
	public TreeNode getMoveToselectedNode() {
		return moveToselectedNode;
	}
	public void setMoveToselectedNode(TreeNode moveToselectedNode) {
		this.moveToselectedNode = moveToselectedNode;
	}
	public TreeNode getMovetoRootNode() {
		return movetoRootNode;
	}
	public void setMovetoRootNode(TreeNode movetoRootNode) {
		this.movetoRootNode = movetoRootNode;
	}
	public OrganizationService getOrganizationService() {
		return organizationService;
	}
	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}
	public Property<CategorySortValue> getSelectedSortValue() {
		return selectedSortValue;
	}
	public void setSelectedSortValue(Property<CategorySortValue> selectedSortValue) {
		this.selectedSortValue = selectedSortValue;
	}
	public Property<SortType> getSelectedSortType() {
		return selectedSortType;
	}
	public void setSelectedSortType(Property<SortType> selectedSortType) {
		this.selectedSortType = selectedSortType;
	}
	public List<Property<CategorySortValue>> getSortCategoryValues() {
		return sortCategoryValues;
	}
	public void setSortCategoryValues(
			List<Property<CategorySortValue>> sortCategoryValues) {
		this.sortCategoryValues = sortCategoryValues;
	}
	public List<Property<SortType>> getSortTypes() {
		return sortTypes;
	}
	public void setSortTypes(List<Property<SortType>> sortTypes) {
		this.sortTypes = sortTypes;
	}
	
	@PostConstruct
    public void initialize() {
		initSortValue();
		
		String categoryId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("categoryId");
		if (StringUtils.isNotEmpty(categoryId)) {
			this.selectedCategory = categoryService.getCategoryAndSecurityById(categoryId, FaceContextUtils.getLoginUser());
		}
		
		this.root = getRootCategory();
			
    }
	
	private void initSortValue() {
		this.selectedSortValue = new Property<CategorySortValue>(CategorySortValue.NAME.name(), CategorySortValue.NAME);
		this.sortCategoryValues = new ArrayList<Property<CategorySortValue>>();
		this.sortCategoryValues.add(selectedSortValue);
		this.sortCategoryValues.add(new Property<CategorySortValue>(CategorySortValue.DATE_CREATED.name(), CategorySortValue.DATE_CREATED));
		this.sortCategoryValues.add(new Property<CategorySortValue>(CategorySortValue.DATE_UPDATED.name(), CategorySortValue.DATE_UPDATED));
		
		this.selectedSortType = new Property<SortType>(SortType.ASC.name(), SortType.ASC);
		this.sortTypes = new ArrayList<Property<SortType>>();
		this.sortTypes.add(selectedSortType);
		this.sortTypes.add(new Property<SortType>(SortType.DESC.name(), SortType.DESC));
	}
	
	public void doReloadCategoryTree() {
		this.root = getRootCategory();
	}
	/**
	 * Recursive method: Create category tree node
	 * @param parent @Category
	 * @param parentNode @TreeNode
	 * @return
	 */
	private boolean createCategoryTreeNode(Category parent, TreeNode parentNode) {
		List<Category> lstSubCategory = categoryService.getSubCategories(parent, this.selectedSortType.getValue(), this.selectedSortValue.getValue());
		
		boolean isExpanded = false;
		if (!CollectionUtils.isEmpty(lstSubCategory)) {
			for (Category subCategory : lstSubCategory) {
				
				//check view security of subCategory
				CategorySecurity security = DomainSecurityChecker.checkCategorySecurity(FaceContextUtils.getLoginUser(), subCategory);
				subCategory.setFinalSecurity(security);
				
				if (subCategory.getFinalSecurity().isCanViewCategory() || subCategory.getFinalSecurity().isCanSetupSecurity()) {
					TreeNode childNode = new DefaultTreeNode(subCategory, parentNode);
					boolean createSubNodeResult = createCategoryTreeNode(subCategory, childNode);
					isExpanded = (isExpanded || createSubNodeResult);
					if (!isExpanded) {
						Category selectedCategory = this.selectedCategory;
						if (this.selectedCategory != null &&selectedCategory.getId() != null 
								&& subCategory.getId().toString().equals(selectedCategory.getId().toString())) {
							isExpanded = true;
							this.selectedNode = childNode;
							this.selectedNode.setSelected(true);
						}
					}
					childNode.setExpanded(createSubNodeResult);
				}
			}
		}
		return isExpanded;
	}
	
	private void doRefreshCategoryDetail() {
		CategoryDetailLayoutBean bean = (CategoryDetailLayoutBean) FaceContextUtils.getViewScopeBean("categoryDetailLayoutBean");
		bean.doRefresh();
	}
	
	/**
	 * Create root category, this method will call the recursive method to create whole category tree
	 * @return @TreeNode
	 */
	private TreeNode getRootCategory(){
		TreeNode hideRootNode = new DefaultTreeNode(new Category("", "root", "hidden root"), null);
		
		hideRootNode.setExpanded(true);
		
		User loginUser = FaceContextUtils.getLoginUser();
		Organization organization = loginUser.getOrganization();
		
		Category rootCategory = categoryService.getRootCategoryAndSecurityByOrganization(organization.getId().toString(), FaceContextUtils.getLoginUser());
		
		if (rootCategory != null) {
			
			if (rootCategory != null) {
				if (rootCategory.getFinalSecurity().isCanViewCategory() || rootCategory.getFinalSecurity().isCanSetupSecurity()) {
					TreeNode mainCategoryNode = new DefaultTreeNode(rootCategory, hideRootNode);
					createCategoryTreeNode(rootCategory, mainCategoryNode);
					if (this.selectedCategory != null && rootCategory.getId().equals(this.selectedCategory.getId())) {
						mainCategoryNode.setSelected(true);
					}
					mainCategoryNode.setExpanded(true);
				}
			}
		}
		return hideRootNode;
	}
	
	/**
	 * Reset the selected category to new
	 * @param event
	 */
	public void doAddCategory(ActionEvent event){
		this.selectedCategory = new Category();
		this.editCategoryDialogHeader = "New Category";
	}
	
	/**
	 * Set selectedCategory
	 * @param event
	 */
	public void doRenameCategory(ActionEvent event) {
		this.selectedCategory = (Category) this.selectedNode.getData();
		this.editCategoryDialogHeader = "Rename Category";
	}
	
	/**
	 * Save category (new and edit mode)
	 * @param event
	 */
	public void doSaveCategory(ActionEvent event){
		try {
			boolean isNew = (this.selectedCategory.getId() == null);
			User loginUser = FaceContextUtils.getLoginUser();
			if (isNew) {
				Category parent = (Category) selectedNode.getData();
				this.selectedCategory.setParent(parent);
				this.selectedCategory.setOrganization(FaceContextUtils.getLoginUser().getOrganization());
				
				//set default security for user who created the category
				//List<CategorySecurity> security = new ArrayList<CategorySecurity>();	
				//defaultSecurity.setUser(loginUser);				
				//this.selectedCategory.setCategorySecurities(new ArrayList<CategorySecurity>());
				//this.selectedCategory.getCategorySecurities().add(defaultSecurity);
				
				this.selectedCategory.setLocked(false);			
				this.selectedCategory = categoryService.addNewCategory(loginUser, selectedCategory);
			}else {
				this.selectedCategory.setLocked(false);			
				this.selectedCategory = categoryService.saveChangedCategory(loginUser, selectedCategory);
			}
			
			this.root = getRootCategory();
			doRefreshCategoryDetail();
			
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage("Save Category Successfully!"));
		}catch (DataAccessException e) {	
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error when create new category!",null));
			
		}
	}
	
	/**
	 * Delete category
	 * @param event
	 */
	public void doDeleteCategory(ActionEvent event) {
		try {
			User loginUser = FaceContextUtils.getLoginUser();
			
			categoryService.delete(loginUser, this.selectedCategory, false);
			if (this.selectedCategory.getParent() == null) {
				this.selectedNode = null;
				this.selectedCategory = null;
			}else {
				this.selectedNode = this.selectedNode.getParent();
				this.selectedCategory = (Category) this.selectedNode.getData();
			}
			this.root = getRootCategory();
			doRefreshCategoryDetail();
			
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage("Delete Category Successfully!"));
		}catch (DataAccessException e) {	
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error when create new category!",null));
			
		} catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
		}
	}

	/**
	 * Refresh file list of selected category
	 * @param event
	 */
	public void onNodeSelect(NodeSelectEvent event) {
		this.selectedNode = event.getTreeNode();
		this.selectedCategory = (Category) this.selectedNode.getData();
		this.selectedCategory = categoryService.getCategoryAndSecurityById(this.selectedCategory.getId().toString(), FaceContextUtils.getLoginUser());
		if (!event.isContextMenu()) {
			doRefreshCategoryDetail();
		}
	}
	
	public void onNodeUnSelect(NodeUnselectEvent event) {
		this.selectedNode = null;
		this.selectedCategory = null;	
		doRefreshCategoryDetail();
	}
	
	public void onMoveToNodeSelect(NodeSelectEvent event) {
		Category node = (Category) event.getTreeNode().getData();
		if (node.getId().equals(this.selectedCategory.getId())) {
			event.getTreeNode().setSelected(false);
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Cannot move the selected category into itself!",null));
		}else {
			this.moveToselectedNode = event.getTreeNode();
		}
	}
	
	public void onMoveToNodeUnSelect(NodeUnselectEvent event) {
		this.moveToselectedNode = new DefaultTreeNode(null, null);
	}
	
	/**
	 * Check the category node is selected or not
	 * @return
	 */
	public boolean isSelectCategoryNode() {
		boolean isSelected = (this.selectedNode != null && this.selectedNode.getData() != null);
		if(isSelected) {
			Category category = (Category) this.selectedNode.getData();
			return category.getId() != null;
		}
		return false;
	}
	
	public boolean isSelectMoveToCategoryNode() {
		return this.moveToselectedNode != null && this.moveToselectedNode.getData() != null;
	}
	
	public void doOpenMoveToDialog(ActionEvent event) {
		this.movetoRootNode = getRootCategory();
		this.moveToselectedNode = new DefaultTreeNode(null, null);
	}
	
	public void doMoveCategory(ActionEvent event) {
		try {
			Category parent = (Category) moveToselectedNode.getData();
			this.selectedCategory.setParent(parent);
			this.selectedCategory.setLocked(false);
			selectedCategory = categoryService.saveMovedCategory(FaceContextUtils.getLoginUser(), selectedCategory);
			
			this.root = getRootCategory();
			this.selectedNode.setSelected(true);
			
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage("Move Category Successfully!"));
		} catch (DataAccessException e) {	
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();  
	        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error when create new category!",null));
		}
	}
}
