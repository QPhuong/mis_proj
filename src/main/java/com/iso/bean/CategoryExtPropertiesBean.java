package com.iso.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.extensions.model.dynaform.DynaFormControl;
import org.primefaces.extensions.model.dynaform.DynaFormLabel;
import org.primefaces.extensions.model.dynaform.DynaFormModel;
import org.primefaces.extensions.model.dynaform.DynaFormRow;

import com.iso.constant.MetadataControlType;
import com.iso.domain.Category;
import com.iso.domain.generic.Metadata;
import com.iso.service.CategoryService;
import com.iso.service.impl.CategoryServiceImpl;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="categoryExtPropertiesBean")
@ViewScoped
public class CategoryExtPropertiesBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private Category selectedCategory;
	
	private String newPropertyName;
	private MetadataControlType newControlType;
	
	private List<SelectItem> controlTypeSelectItems;
	
	private List<Metadata<?>> metadataLst;
	
	@ManagedProperty(value="#{categoryService}")
	private CategoryService categoryService;
	
	private DynaFormModel model;
	
	public Category getSelectedCategory() {
		return selectedCategory;
	}
	public void setSelectedCategory(Category selectedCategory) {
		this.selectedCategory = selectedCategory;
	}
	public CategoryService getCategoryService() {
		return categoryService;
	}
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	public List<SelectItem> getControlTypeSelectItems() {
		return controlTypeSelectItems;
	}
	public void setControlTypeSelectItems(List<SelectItem> controlTypeSelectItems) {
		this.controlTypeSelectItems = controlTypeSelectItems;
	}
	public String getNewPropertyName() {
		return newPropertyName;
	}
	public void setNewPropertyName(String newPropertyName) {
		this.newPropertyName = newPropertyName;
	}
	public MetadataControlType getNewControlType() {
		return newControlType;
	}
	public void setNewControlType(MetadataControlType newControlType) {
		this.newControlType = newControlType;
	}
	public DynaFormModel getModel() {
		return model;
	}
	public void setModel(DynaFormModel model) {
		this.model = model;
	}

	@PostConstruct
	public void initilize() {
		initControlTypeSelectItem();
		model = new DynaFormModel();		
	}
	
	public void doReloaExtProperties() {
		Object bean = FaceContextUtils.getViewScopeBean("categoryTreeBean");
		if (bean != null) {
			CategoryTreeBean treeBean = (CategoryTreeBean) bean;
			this.selectedCategory = categoryService.getCategoryAndSecurityById(treeBean.getSelectedCategory().getId().toString(), FaceContextUtils.getLoginUser());
			buildDynaFormModel();
		}
	}
	
	private void initControlTypeSelectItem() {
		this.controlTypeSelectItems = new ArrayList<SelectItem>();
		for (MetadataControlType type : MetadataControlType.values()) {
			this.controlTypeSelectItems.add(new SelectItem(type, type.toDisplayString()));
		}
	}
	
	private void buildDynaFormModel() {
		model = new DynaFormModel();
		if (CollectionUtils.isNotEmpty(this.selectedCategory.getProperties())){
			for (Metadata<?> data : this.selectedCategory.getProperties()) {
				data.setEditable(this.selectedCategory.getFinalSecurity().isCanEditCategory());
				DynaFormRow row = model.createRegularRow();
				DynaFormLabel label = row.addLabel(data.getName());
				DynaFormControl control = row.addControl(data, data.getControlType().toString());  
				label.setForControl(control);
				row.addControl(data, "delete");
			}
		}
	}
	
	public void doOpenNewPropDialog() {
		this.newPropertyName = "";
		this.newControlType = MetadataControlType.INPUT;
	}
	
	public void doAddNewExtProperty() {
		if (CollectionUtils.isEmpty(this.selectedCategory.getProperties())){
			this.selectedCategory.setProperties(new ArrayList<Metadata<?>>());
		}
		this.selectedCategory.getProperties().add(Metadata.newInstance(newPropertyName, newControlType));
		buildDynaFormModel();
	}
	
	public void doSaveExtProperties() {
		try {
			this.selectedCategory.getProperties().clear();
			for (DynaFormControl dynaFormControl : model.getControls()) {
				if(!dynaFormControl.getType().equalsIgnoreCase("delete")){
					this.selectedCategory.getProperties().add((Metadata<?>) dynaFormControl.getData());
				}
			}
			categoryService.saveCategoryExtProperties(FaceContextUtils.getLoginUser(), selectedCategory);
			FaceContextUtils.showInfoMessage("Save ext. properties successfully!", null);
		} catch(Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when saving ext. properties !", e.getStackTrace().toString());
		}
	}
	
	public void doResetExtProperties() {
		this.selectedCategory = categoryService.getCategoryById(this.selectedCategory.getId().toString());
		buildDynaFormModel();
	}
	
	public void doDeleteProperty(Metadata<?> metadata) {
		this.selectedCategory.getProperties().remove(metadata);
		buildDynaFormModel();
	}
}
