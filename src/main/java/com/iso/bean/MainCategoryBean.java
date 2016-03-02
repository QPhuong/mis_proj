package com.iso.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.xml.bind.JAXBException;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;
import org.springframework.dao.DataAccessException;

import com.iso.constant.CategorySortValue;
import com.iso.constant.SortType;
import com.iso.domain.IsoDocument;
import com.iso.domain.MainCategory;
import com.iso.domain.MainCategoryType;
import com.iso.domain.generic.Property;
import com.iso.exception.BusinessException;
import com.iso.face.converter.MainCategoryTypeConverter;
import com.iso.jaxb.category.DefaultMainCategory;
import com.iso.jaxb.category.DefaultMainCategoryReader;
import com.iso.model.IsoFile;
import com.iso.service.IsoDocumentService;
import com.iso.service.MainCategoryService;
import com.iso.service.MainCategoryTypeService;
import com.iso.service.OrganizationService;
import com.iso.util.FaceContextUtils;
import com.mongodb.gridfs.GridFSDBFile;

@ManagedBean(name="mainCategoryBean")
@ViewScoped
public class MainCategoryBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int activeTabIndex;
	
	private int MAIN_CATEGORY_DATA_TAB = 0;
	private int MAIN_CATEGORY_TYPE_TAB = 1;
	
	private List<MainCategoryType> categoryTypes;
	private MainCategoryType selectedCategoryType;
	
	private TreeNode root;
	private TreeNode selectedNode;
	private boolean isTemporaryTreeNode = false;
	
	private TreeNode movetoRootNode;
	private TreeNode moveToselectedNode;
	
	private MainCategory selectedMainCategory;
	private DefaultMainCategory defaultMainCategory;

	private IsoDocument isoDocument;
	private IsoFile temporaryFile;
	private List<IsoFile> lstTemporaryFile;
	
	private StreamedContent downloadFile;
	
	private Property<CategorySortValue> selectedSortValue;
	private Property<SortType> selectedSortType;
	private List<Property<CategorySortValue>> sortCategoryValues;
	private List<Property<SortType>> sortTypes;
	
	@ManagedProperty(value="#{mainCategoryTypeService}")
	private MainCategoryTypeService mainCategoryTypeService;
	
	@ManagedProperty(value="#{mainCategoryService}")
	private MainCategoryService mainCategoryService;
	
	@ManagedProperty(value="#{isoDocumentService}")
	private IsoDocumentService isoDocumentService;
	
	@ManagedProperty(value="#{organizationService}")
	private OrganizationService organizationService;	
	
	
	public int getActiveTabIndex() {
		return activeTabIndex;
	}
	public void setActiveTabIndex(int activeTabIndex) {
		this.activeTabIndex = activeTabIndex;
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
	public TreeNode getMovetoRootNode() {
		return movetoRootNode;
	}
	public void setMovetoRootNode(TreeNode movetoRootNode) {
		this.movetoRootNode = movetoRootNode;
	}
	public TreeNode getMoveToselectedNode() {
		return moveToselectedNode;
	}
	public void setMoveToselectedNode(TreeNode moveToselectedNode) {
		this.moveToselectedNode = moveToselectedNode;
	}
	public MainCategory getSelectedMainCategory() {
		return selectedMainCategory;
	}
	public void setSelectedMainCategory(MainCategory selectedMainCategory) {
		this.selectedMainCategory = selectedMainCategory;
	}
	public MainCategoryTypeService getMainCategoryTypeService() {
		return mainCategoryTypeService;
	}
	public void setMainCategoryTypeService(MainCategoryTypeService mainCategoryTypeService) {
		this.mainCategoryTypeService = mainCategoryTypeService;
	}
	public MainCategoryService getMainCategoryService() {
		return mainCategoryService;
	}
	public void setMainCategoryService(MainCategoryService mainCategoryService) {
		this.mainCategoryService = mainCategoryService;
	}
	public OrganizationService getOrganizationService() {
		return organizationService;
	}
	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}
	public IsoFile getTemporaryFile() {
		return temporaryFile;
	}
	public void setTemporaryFile(IsoFile temporaryFile) {
		this.temporaryFile = temporaryFile;
	}
	public List<IsoFile> getLstTemporaryFile() {
		return lstTemporaryFile;
	}
	public void setLstTemporaryFile(List<IsoFile> lstTemporaryFile) {
		this.lstTemporaryFile = lstTemporaryFile;
	}
	public IsoDocument getIsoDocument() {
		return isoDocument;
	}
	public void setIsoDocument(IsoDocument isoDocument) {
		this.isoDocument = isoDocument;
	}
	public boolean isSelected() {
		return ((this.selectedNode != null && this.selectedNode.getData() != null) 
				|| (this.selectedMainCategory != null && this.selectedMainCategory.getId() == null));
	}
	public boolean isTemporaryTreeNode() {
		return isTemporaryTreeNode;
	}
	public void setTemporaryTreeNode(boolean isTemporaryTreeNode) {
		this.isTemporaryTreeNode = isTemporaryTreeNode;
	}
	public List<MainCategoryType> getCategoryTypes() {
		return categoryTypes;
	}
	public void setCategoryTypes(List<MainCategoryType> categoryTypes) {
		this.categoryTypes = categoryTypes;
	}
	public MainCategoryType getSelectedCategoryType() {
		return selectedCategoryType;
	}
	public void setSelectedCategoryType(MainCategoryType selectedCategoryType) {
		this.selectedCategoryType = selectedCategoryType;
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
	public void setSortCategoryValues(List<Property<CategorySortValue>> sortCategoryValues) {
		this.sortCategoryValues = sortCategoryValues;
	}
	public List<Property<SortType>> getSortTypes() {
		return sortTypes;
	}
	public void setSortTypes(List<Property<SortType>> sortTypes) {
		this.sortTypes = sortTypes;
	}
	public StreamedContent getDownloadFile() {
		return downloadFile;
	}
	public void setDownloadFile(StreamedContent downloadFile) {
		this.downloadFile = downloadFile;
	}
	public IsoDocumentService getIsoDocumentService() {
		return isoDocumentService;
	}
	public void setIsoDocumentService(IsoDocumentService isoDocumentService) {
		this.isoDocumentService = isoDocumentService;
	}
	
	public boolean isExistingMainCategory() {
		if (this.selectedCategoryType != null && this.selectedCategoryType.getId() != null) {
			MainCategory rootMainCategory = mainCategoryService.getRootCategoryByCategoryType(selectedCategoryType);
			return (rootMainCategory != null && rootMainCategory.getId() != null);
		}
		return false;
	}
	
	private void resetSelectedItem() {
		this.selectedMainCategory = null;
		if (this.selectedNode != null) {
			this.selectedNode.setSelected(false);
		}
		this.selectedNode = null;
	}
	
	public boolean isRenderMainCategoryDetailTab() {
		return (this.activeTabIndex == MAIN_CATEGORY_DATA_TAB);
	}
	
	public boolean isRenderMainCategoryTypeTab() {
		return (this.activeTabIndex == MAIN_CATEGORY_TYPE_TAB);
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
	
	@PostConstruct
	public void initialize() {
		try{
			defaultMainCategory = loadDefaultMainCategoryFromXML();
			
			this.categoryTypes = mainCategoryTypeService.getAllMainCategoryType();
			if (CollectionUtils.isNotEmpty(this.categoryTypes)) {
				this.selectedCategoryType = this.categoryTypes.get(0); 
			}
			
			this.lstTemporaryFile = new ArrayList<IsoFile>();
			this.activeTabIndex = 0;
			
			initSortValue();
			
		}catch(DataAccessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when loading main category: " + e.getMessage(), e.getStackTrace().toString());
		}
	}
	
	public void reloadCategoryTypeList() {
		this.categoryTypes = mainCategoryTypeService.getAllMainCategoryType();
		
		Object bean = FaceContextUtils.getViewScopeBean("mainCategoryTypeConverter");
		if (bean != null) {
			MainCategoryTypeConverter converter = (MainCategoryTypeConverter) bean;
			converter.setCategoryTypeList(this.categoryTypes);
		}
		
		boolean canReselect = false;
		if (CollectionUtils.isNotEmpty(this.categoryTypes)) {
			for (MainCategoryType type : this.categoryTypes) {
				if (this.selectedCategoryType != null && type.getId().equals(this.selectedCategoryType.getId())) {
					this.selectedCategoryType = type;
					canReselect = true;
					break;
				}
			}
			if (!canReselect) {
				this.selectedCategoryType = this.categoryTypes.get(0);
			}
		}else {
			this.selectedCategoryType = null;
		}
		
		if (!canReselect) {
			doChangeCategoryType();
		}
		
	}
	
	public void onTabChange(TabChangeEvent event) {
		this.activeTabIndex = event.getComponent().getChildren().indexOf(event.getTab());
		if (this.activeTabIndex == MAIN_CATEGORY_DATA_TAB) {
			reloadCategoryTypeList();
		}
		
	}
	
	private DefaultMainCategory loadDefaultMainCategoryFromXML() {
		try {
			return DefaultMainCategoryReader.generateMainCategory();
		} catch (JAXBException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when reading default category: " + e.getMessage(), e.getStackTrace().toString());
		} catch (IOException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when reading default category: " + e.getMessage(), e.getStackTrace().toString());
		}
		return null;
	}
	
	private TreeNode buildDefaultMainCategoryTree(DefaultMainCategory parent, MainCategoryType categoryType) {
		
		TreeNode hideRootNode = new DefaultTreeNode(new MainCategory(), null);
		hideRootNode.setExpanded(true);
		
		MainCategory rootMainCategory = new MainCategory(this.defaultMainCategory, null, this.selectedCategoryType);
		TreeNode startNode = new DefaultTreeNode(rootMainCategory, hideRootNode);
		startNode.setExpanded(true);
		
		/*if (this.selectedMainCategory != null && rootMainCategory.getSequence() == this.selectedMainCategory.getSequence()) {
			startNode.setSelected(true);
			this.selectedNode = startNode;
		}*/
		this.createSubDefaultMainCategoryTree(this.defaultMainCategory, startNode, this.selectedCategoryType);
		
		this.isTemporaryTreeNode = true;
		return hideRootNode;
	}
	
	private TreeNode createSubDefaultMainCategoryTree(DefaultMainCategory parent, TreeNode parentNode, MainCategoryType categoryType) {
		List<DefaultMainCategory> lstDefaultCategory = parent.getSubcategories();
		
		if (!CollectionUtils.isEmpty(lstDefaultCategory)) {
			for (DefaultMainCategory subDefaultCategory : lstDefaultCategory) {
				
				MainCategory parentMainCategory = (MainCategory) parentNode.getData();
				MainCategory subMainCategory = new MainCategory(subDefaultCategory, parentMainCategory, categoryType);
				
				TreeNode childNode = new DefaultTreeNode(subMainCategory, parentNode);
				childNode.setExpanded(true);
				/*if (this.selectedMainCategory != null && subMainCategory.getSequence() == this.selectedMainCategory.getSequence()) {
					childNode.setSelected(true);
					this.selectedNode = childNode;
				}*/
				this.createSubDefaultMainCategoryTree(subDefaultCategory, childNode, categoryType);
			}
		}
		return parentNode;
	}
	
	private TreeNode buildMainCategoryTree(MainCategoryType categoryType) {
		TreeNode hideRootNode = new DefaultTreeNode(new MainCategory(), null);
		hideRootNode.setExpanded(true);
		
		MainCategory rootMainCategory = mainCategoryService.getRootCategoryByCategoryType(categoryType);
		
		if (rootMainCategory != null) {
			TreeNode startNode = new DefaultTreeNode(rootMainCategory, hideRootNode);
			startNode.setExpanded(true);
			if (this.selectedMainCategory != null && rootMainCategory.getId().equals(this.selectedMainCategory.getId())) {
				startNode.setSelected(true);
				this.selectedNode = startNode;
			}
			createSubCategoryTreeNode(rootMainCategory, startNode);
		}
		this.isTemporaryTreeNode = false;
		return hideRootNode;
	}
	
	private void createSubCategoryTreeNode(MainCategory parent, TreeNode parentNode) {
		
		List<MainCategory> lstSubCategory = mainCategoryService.getSubMainCategoryByParent (parent, this.selectedSortValue.getValue(), this.selectedSortType.getValue());
				
		if (!CollectionUtils.isEmpty(lstSubCategory)) {
			for (MainCategory subcategory : lstSubCategory) {
				TreeNode childNode = new DefaultTreeNode(subcategory, parentNode);
				childNode.setExpanded(true);
				if (this.selectedMainCategory != null && subcategory.getId().equals(this.selectedMainCategory.getId())) {
					childNode.setSelected(true);
					this.selectedNode = childNode;
				}
				this.createSubCategoryTreeNode(subcategory, childNode);
			}
		}
	}
	
	public void doLoadDefaultCategoryTree(ActionEvent event) {
		this.root = buildDefaultMainCategoryTree(this.defaultMainCategory, this.selectedCategoryType);
		resetSelectedItem();
	}
	
	public void doLoadCategoryTree(ActionEvent event) {
		this.root = buildMainCategoryTree(this.selectedCategoryType);
		resetSelectedItem();
	}
	
	public void doChangeCategoryType() {
		this.root = null;
		this.lstTemporaryFile = new ArrayList<IsoFile>();
		this.isTemporaryTreeNode = false;
		resetSelectedItem();
	}
	
	public void doAddCategory(ActionEvent event){
		this.selectedMainCategory = new MainCategory();
		this.lstTemporaryFile = new ArrayList<IsoFile>();
	}
	
	public void doEditCategory(ActionEvent event) {
		this.selectedMainCategory = (MainCategory) this.selectedNode.getData();
		this.selectedMainCategory = mainCategoryService.getMainCategoryById(this.selectedMainCategory.getId().toString());
		this.lstTemporaryFile = new ArrayList<IsoFile>();
	}
	
	public void doOpenMoveToDialog(ActionEvent event) {
		
	}

	public void doResetCategory(ActionEvent event) {
		if (this.selectedMainCategory != null && this.selectedMainCategory.getId() != null) {
			this.selectedMainCategory = mainCategoryService.getMainCategoryById(this.selectedMainCategory.getId().toString());
			this.lstTemporaryFile = new ArrayList<IsoFile>();
		}else {
			this.selectedMainCategory = new MainCategory();
			this.lstTemporaryFile = new ArrayList<IsoFile>();
		}
	}
	
	private void saveAllSubCategory(TreeNode parent) throws IOException {
		try {
			if (parent != null && CollectionUtils.isNotEmpty(parent.getChildren())) {
				for(TreeNode node: parent.getChildren()) {
					MainCategory category = (MainCategory) node.getData();
					
					List<IsoFile> lstTemplate = new ArrayList<IsoFile>();
					if(CollectionUtils.isNotEmpty(category.getLstTemplateDefault())) {
						for(File file : category.getLstTemplateDefault()) {
							if (file.exists() && file.isFile()) {
								lstTemplate.add(new IsoFile(file));
							}
						}
					}
					mainCategoryService.save(FaceContextUtils.getLoginUser(), category, lstTemplate);
					saveAllSubCategory(node);
				}
			}
		} catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
		}
	}
	
	public void doSaveAllTemporaryCategories() {
		try {
			if (this.isTemporaryTreeNode) {
				//the first node is empty node
				MainCategory rootCategory = null;
				List<TreeNode> lstSubCategory = this.root.getChildren(); 
				
				if (CollectionUtils.isNotEmpty(lstSubCategory)) {
					for(TreeNode node: lstSubCategory) {
						rootCategory = (MainCategory) node.getData();
						rootCategory.setRoot(true);
						List<IsoFile> lstTemplate = new ArrayList<IsoFile>();
						if(CollectionUtils.isNotEmpty(rootCategory.getLstTemplateDefault())) {
							for(File file : rootCategory.getLstTemplateDefault()) {
								if (file.exists() && file.isFile()) {
									lstTemplate.add(new IsoFile(file));
								}
							}
						}
						rootCategory = mainCategoryService.save(FaceContextUtils.getLoginUser(), rootCategory, lstTemplate);
						saveAllSubCategory(node);
					}
				}
				
				mainCategoryTypeService.save(FaceContextUtils.getLoginUser(), selectedCategoryType);
			}
			this.lstTemporaryFile = new ArrayList<IsoFile>();
			this.isTemporaryTreeNode = false;
			FaceContextUtils.showInfoMessage("Save main category successfully!", null);
			
		} catch (IOException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when saving main category: " + e.getMessage(), e.getStackTrace().toString());
		} catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
		}
	}
	
	public void doSaveCategory(ActionEvent event){
		try {
			if (isTemporaryTreeNode) {
				MainCategory selectedCat = (MainCategory) this.selectedNode.getData();
				
				if (selectedCat.getId() == null) { //parent is a temporary categories
					if(CollectionUtils.isNotEmpty(this.lstTemporaryFile)) {
						for(IsoFile tempIsoFile : this.lstTemporaryFile) {
							File file = new File(tempIsoFile.getPath());
							if (file.exists() && file.isFile()) {
								this.selectedMainCategory.getLstTemplateDefault().add(file);
							}
						}
					}
					((DefaultTreeNode) this.selectedNode).setData(this.selectedMainCategory);
					FaceContextUtils.showWarningMessage("btnSave", "This category is saved temporary. Please save all temporary categories!", null);
					return;
				}
				
			} else {
				boolean isNew = (this.selectedMainCategory.getId() == null);
				if (isNew) {
					MainCategory parent = (this.selectedNode != null) ? (MainCategory) this.selectedNode.getData() : null;
					if(parent == null) {
						this.selectedMainCategory.setRoot(true);
						this.selectedMainCategory.setType(this.selectedCategoryType);
					}
					this.selectedMainCategory.setParent(parent);
				} 
				this.selectedMainCategory = mainCategoryService.save(FaceContextUtils.getLoginUser(), this.selectedMainCategory, this.lstTemporaryFile);
				
				
				this.root = buildMainCategoryTree(this.selectedCategoryType);
				this.lstTemporaryFile = new ArrayList<IsoFile>();
				FaceContextUtils.showInfoMessage("Save main category successfully!", null);
			}
		}catch(DataAccessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when saving main category: " + e.getMessage(), e.getStackTrace().toString());
		} catch (IOException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when saving main category: " + e.getMessage(), e.getStackTrace().toString());
		} catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
		}
	}
	
	public void doMoveCategory(ActionEvent event) {
		
	}
	
	public void doDeleteCategory(ActionEvent event) {
		try {
			if (this.selectedMainCategory != null && this.selectedMainCategory.getId() != null) {
				mainCategoryService.delete(FaceContextUtils.getLoginUser(), this.selectedMainCategory);
				
				doLoadCategoryTree(null);
				
				this.lstTemporaryFile = new ArrayList<IsoFile>();
				FaceContextUtils.showInfoMessage("Save main category successfully!", null);
				
			} else if (isTemporaryTreeNode) {
				TreeNode parent = this.selectedNode.getParent();
				parent.getChildren().remove(this.selectedNode);
				this.lstTemporaryFile = new ArrayList<IsoFile>();
			}
		} catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
		}
	}
	
	public void onNodeSelect(NodeSelectEvent event) {
		try {
			this.selectedMainCategory = (MainCategory) this.selectedNode.getData();
			this.lstTemporaryFile = new ArrayList<IsoFile>();
			if (this.selectedMainCategory.getId() != null) {
				this.selectedMainCategory = mainCategoryService.getMainCategoryById(this.selectedMainCategory.getId().toString());
			}else {
				if(CollectionUtils.isNotEmpty(this.selectedMainCategory.getLstTemplateDefault())) {
					for(File file : this.selectedMainCategory.getLstTemplateDefault()) {
						if (file.exists() && file.isFile()) {
							this.lstTemporaryFile.add(new IsoFile(file));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
		}
	}
	
	public void onNodeUnSelect(NodeUnselectEvent event) {
		resetSelectedItem();
	}
	
	public void onMoveToNodeSelect(NodeSelectEvent event) {
		
	}
	
	public void onMoveToNodeUnSelect(NodeUnselectEvent event) {
		
	}
	
	public void doUpload(FileUploadEvent  event) {
		try {
			UploadedFile uploadedFile = event.getFile();
			this.lstTemporaryFile.add(new IsoFile(uploadedFile));
		} catch (IOException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when reading uploaded file", e.getStackTrace().toString());
		}
	}
	
	public void doDeleteTemporaryFile() {
		this.lstTemporaryFile.remove(this.temporaryFile);
	}
	
	public void doDeleteTemplateFile() {
		this.selectedMainCategory.getLstTemplates().remove(this.isoDocument);
	}
	
	public void doDownloadTemplate(ActionEvent event) {
    	try {
    		if (this.isoDocument != null && this.isoDocument.getId() != null) {
    			GridFSDBFile gridFile = isoDocumentService.getGridFSFile(FaceContextUtils.getLoginUser(), this.isoDocument);
    			this.downloadFile = new DefaultStreamedContent(gridFile.getInputStream(), gridFile.getContentType(), this.isoDocument.getFileName());
    		}else {
    			this.downloadFile = null;
    		}
    	} catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when download document!", e.getStackTrace().toString());
		}
    }
	
	public void doDownloadTempTemplate(ActionEvent event) {
    	try {
    		if (this.temporaryFile != null) {
    			this.downloadFile = new DefaultStreamedContent(this.temporaryFile.getInputStream(), this.temporaryFile.getContentType(), this.temporaryFile.getFileName());
    		}else {
    			this.downloadFile = null;
    		}
    	} catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when download document!", e.getStackTrace().toString());
		}
    }
}
