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
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.dao.DataAccessException;

import com.iso.domain.Category;
import com.iso.domain.CategorySecurity;
import com.iso.domain.IsoDocument;
import com.iso.domain.Organization;
import com.iso.service.CategoryService;
import com.iso.service.IsoDocumentService;
import com.iso.util.DomainSecurityChecker;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="documentPropertiesBean")
@ViewScoped
public class DocumentPropertiesBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private IsoDocument selectedDoc;
	private Category additionalCategory;
	
	private TreeNode root;
	private TreeNode selectedNode;
	
	private String versionComment = "";
	
	@ManagedProperty(value="#{isoDocumentService}")
	private IsoDocumentService isoDocumentService;

	@ManagedProperty(value="#{categoryService}")
	private CategoryService categoryService;
	
	public IsoDocument getSelectedDoc() {
		return selectedDoc;
	}
	public void setSelectedDoc(IsoDocument selectedDoc) {
		this.selectedDoc = selectedDoc;
	}
	public IsoDocumentService getIsoDocumentService() {
		return isoDocumentService;
	}
	public void setIsoDocumentService(IsoDocumentService isoDocumentService) {
		this.isoDocumentService = isoDocumentService;
	}
	public String getVersionComment() {
		return versionComment;
	}
	public void setVersionComment(String versionComment) {
		this.versionComment = versionComment;
	}
	public Category getAdditionalCategory() {
		return additionalCategory;
	}
	public void setAdditionalCategory(Category additionalCategory) {
		this.additionalCategory = additionalCategory;
	}
	public CategoryService getCategoryService() {
		return categoryService;
	}
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
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
	
	@PostConstruct
	public void initialize() {
		try{
			//doLoadDocumentProperties();
		}catch(DataAccessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when loading document properties!", null);
		}
	}
	
	public void doLoadDocumentProperties() {
		Object bean = FaceContextUtils.getViewScopeBean("documentBean");
		if (bean != null) {
			DocumentBean docBean = (DocumentBean) bean;
			if (docBean.getSelectedDoc() != null && docBean.getSelectedDoc().getId() != null) {
				this.selectedDoc = isoDocumentService.getIsoDocumentAndSecurityById(docBean.getSelectedDoc().getId().toString(), FaceContextUtils.getLoginUser());
				this.versionComment = "";
			}
		}else {
			FaceContextUtils.showErrorMessage("Error when loading document properties!", null);
		}
	}
	
	public void doLoadDeletedDocumentProperties() {
		Object bean = FaceContextUtils.getViewScopeBean("trashDomaintBean");
		if (bean != null) {
			TrashDomaintBean docBean = (TrashDomaintBean) bean;
			if (docBean.getSelectedTrash() != null && docBean.getSelectedTrash().getDomain() != null) {
				if (docBean.getSelectedTrash().getDomain() instanceof IsoDocument) {
					this.selectedDoc = (IsoDocument) docBean.getSelectedTrash().getDomain();
					this.selectedDoc = isoDocumentService.getIsoDocumentAndSecurityById(this.selectedDoc.getId().toString(), FaceContextUtils.getLoginUser());
					this.versionComment = "";
				}
			}
		}else {
			FaceContextUtils.showErrorMessage("Error when loading document properties!", null);
		}
	}
	
	public void doSaveProperties(ActionEvent event) {
		try {
			isoDocumentService.saveDocumentProperties(FaceContextUtils.getLoginUser(), this.selectedDoc, this.versionComment);
			this.versionComment = "";
			FaceContextUtils.showInfoMessage("Save document properties successfully", null);	
		}catch(DataAccessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when saving document properties!", null);
		}
	}
	
	public void doResetProperties(ActionEvent event) {
		doLoadDocumentProperties();
	}
	
	/**
	 * Recursive method: Create category tree node
	 * @param parent @Category
	 * @param parentNode @TreeNode
	 * @return
	 */
	private void createCategoryTreeNode(Category parent, TreeNode parentNode) {
		List<Category> lstSubCategory = categoryService.getSubCategories(parent);
		
		if (!CollectionUtils.isEmpty(lstSubCategory)) {
			for (Category subCategory : lstSubCategory) {
				
				//check view security of subCategory
				CategorySecurity security = DomainSecurityChecker.checkCategorySecurity(FaceContextUtils.getLoginUser(), subCategory);
				subCategory.setFinalSecurity(security);
				
				if (subCategory.getFinalSecurity().isCanViewCategory()) {
					TreeNode childNode = new DefaultTreeNode(subCategory, parentNode);
					createCategoryTreeNode(subCategory, childNode);
				}
			}
		}
	}
	
	/**
	 * Create root category, this method will call the recursive method to create whole category tree
	 * @return @TreeNode
	 */
	public TreeNode getRootCategory(Organization organization){
		TreeNode hideRootNode = new DefaultTreeNode(new Category("", "root", "hidden root"), null);
		
		hideRootNode.setExpanded(true);
		
		Category rootCategory = categoryService.getRootCategoryAndSecurityByOrganization(organization.getId().toString(), FaceContextUtils.getLoginUser());
		
		if (rootCategory != null) {
			
			if (rootCategory != null) {
				if (rootCategory.getFinalSecurity().isCanViewCategory()) {
					TreeNode mainCategoryNode = new DefaultTreeNode(rootCategory, hideRootNode);
					createCategoryTreeNode(rootCategory, mainCategoryNode);
					mainCategoryNode.setExpanded(true);
				}
			}
		}
		return hideRootNode;
	}
	
	public void doOpenAdditionalCategoryDialog() {
		if (this.root == null) {
			this.root = getRootCategory(FaceContextUtils.getLoginUser().getOrganization());
		}
		this.additionalCategory = null;
	}
	
	public void doAddCategory() {
		if (this.selectedDoc.getAdditionalCategories() == null) {
			this.selectedDoc.setAdditionalCategories(new ArrayList<Category>());
			this.selectedDoc.getAdditionalCategories().add(this.additionalCategory);
		}else {
			boolean existing = false;
			if (this.selectedDoc.getCategory().getId().equals(this.additionalCategory.getId())) {
				existing = true;
				return;
			}
			for (Category additionalCate : this.selectedDoc.getAdditionalCategories()) {
				if (additionalCate.getId().equals(this.additionalCategory.getId())) {
					existing = true;
					break;
				}
			}
			if (!existing) {
				this.selectedDoc.getAdditionalCategories().add(this.additionalCategory);
			}
			
		}
		
	}
	
	public void doDeleteCategory() {
		this.selectedDoc.getAdditionalCategories().remove(this.additionalCategory);
	}
	
	public void onNodeSelect(NodeSelectEvent event) {
		this.additionalCategory = (Category) event.getTreeNode().getData();
	}
	
	public void onNodeUnSelect(NodeUnselectEvent event) {
		this.additionalCategory = null;
	}
}
