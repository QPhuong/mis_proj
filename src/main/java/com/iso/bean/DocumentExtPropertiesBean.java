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
import com.iso.domain.IsoDocument;
import com.iso.domain.generic.Metadata;
import com.iso.service.IsoDocumentService;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="documentExtPropertiesBean")
@ViewScoped
public class DocumentExtPropertiesBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private IsoDocument selectedDoc;
	
	private String newPropertyName;
	private MetadataControlType newControlType;
	
	private List<SelectItem> controlTypeSelectItems;
	
	private String versionComment = "";
	
	@ManagedProperty(value="#{isoDocumentService}")
	private IsoDocumentService isoDocumentService;
	
	private DynaFormModel model;
	
	public IsoDocument getSelectedDoc() {
		return selectedDoc;
	}
	public void setSelectedDoc(IsoDocument selectedDoc) {
		this.selectedDoc = selectedDoc;
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
	public String getVersionComment() {
		return versionComment;
	}
	public void setVersionComment(String versionComment) {
		this.versionComment = versionComment;
	}
	public List<SelectItem> getControlTypeSelectItems() {
		return controlTypeSelectItems;
	}
	public void setControlTypeSelectItems(List<SelectItem> controlTypeSelectItems) {
		this.controlTypeSelectItems = controlTypeSelectItems;
	}
	public IsoDocumentService getIsoDocumentService() {
		return isoDocumentService;
	}
	public void setIsoDocumentService(IsoDocumentService isoDocumentService) {
		this.isoDocumentService = isoDocumentService;
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
		Object bean = FaceContextUtils.getViewScopeBean("documentBean");
		if (bean != null) {
			DocumentBean docBean = (DocumentBean) bean;
			this.selectedDoc = isoDocumentService.getIsoDocumentAndSecurityById(docBean.getSelectedDoc().getId().toString(), FaceContextUtils.getLoginUser());
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
		if (CollectionUtils.isNotEmpty(this.selectedDoc.getProperties())){
			for (Metadata<?> data : this.selectedDoc.getProperties()) {
				data.setEditable(this.selectedDoc.canEdit(FaceContextUtils.getLoginUser()));

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
		if (CollectionUtils.isEmpty(this.selectedDoc.getProperties())){
			this.selectedDoc.setProperties(new ArrayList<Metadata<?>>());
		}
		this.selectedDoc.getProperties().add(Metadata.newInstance(newPropertyName, newControlType));
		buildDynaFormModel();
	}
	
	public void doSaveExtProperties() {
		try {
			this.selectedDoc.getProperties().clear();
			for (DynaFormControl dynaFormControl : model.getControls()) {
				if(!dynaFormControl.getType().equalsIgnoreCase("delete")){
					this.selectedDoc.getProperties().add((Metadata<?>) dynaFormControl.getData());
				}
			}
			isoDocumentService.saveDocumentExtProperties(FaceContextUtils.getLoginUser(), selectedDoc, versionComment);
			this.versionComment = "";
			FaceContextUtils.showInfoMessage("Save ext. properties successfully!", null);
		} catch(Exception e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when saving ext. properties !", e.getStackTrace().toString());
		}
	}
	
	public void doResetExtProperties() {
		this.selectedDoc = isoDocumentService.getIsoDocumentAndSecurityById(this.selectedDoc.getId().toString(), FaceContextUtils.getLoginUser());
		this.versionComment = "";
		buildDynaFormModel();
	}
	
	public void doDeleteProperty(Metadata<?> metadata) {
		this.selectedDoc.getProperties().remove(metadata);
		buildDynaFormModel();
	}
}
