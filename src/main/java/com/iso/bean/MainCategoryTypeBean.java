package com.iso.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.springframework.dao.DataAccessException;

import com.iso.domain.MainCategoryType;
import com.iso.exception.BusinessException;
import com.iso.service.MainCategoryTypeService;
import com.iso.service.impl.MainCategoryTypeServiceImpl;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="mainCategoryTypeBean")
@ViewScoped
public class MainCategoryTypeBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private MainCategoryType selectedCategoryType;
	private List<MainCategoryType> categoryTypeLst;
	
	private List<MainCategoryType> categoryTypeFilterLst;
	private String filterText;
	
	private boolean isShownDetail = false;
	
	@ManagedProperty(value="#{mainCategoryTypeService}")
	private MainCategoryTypeService mainCategoryTypeService;
	
	public MainCategoryTypeBean(){
		selectedCategoryType = new MainCategoryType();
	}

	public boolean isShownDetail() {
		return isShownDetail;
	}

	public void setShownDetail(boolean isShownDetail) {
		this.isShownDetail = isShownDetail;
	}

	public MainCategoryType getSelectedCategoryType() {
		return selectedCategoryType;
	}

	public void setSelectedCategoryType(MainCategoryType selectedCategoryType) {
		this.selectedCategoryType = selectedCategoryType;
	}

	public List<MainCategoryType> getCategoryTypeLst() {
		return categoryTypeLst;
	}

	public void setCategoryTypeLst(List<MainCategoryType> categoryTypeLst) {
		this.categoryTypeLst = categoryTypeLst;
		resetFilter();
	}

	public MainCategoryTypeService getMainCategoryTypeService() {
		return mainCategoryTypeService;
	}

	public void setMainCategoryTypeService(MainCategoryTypeService mainCategoryTypeService) {
		this.mainCategoryTypeService = mainCategoryTypeService;
	}
	
	public List<MainCategoryType> getCategoryTypeFilterLst() {
		return categoryTypeFilterLst;
	}

	public void setCategoryTypeFilterLst(
			List<MainCategoryType> categoryTypeFilterLst) {
		this.categoryTypeFilterLst = categoryTypeFilterLst;
	}

	public String getFilterText() {
		return filterText;
	}

	public void setFilterText(String filterText) {
		this.filterText = filterText;
	}

	@PostConstruct
	public void initialize() {
		try{
			doLoadCategoryTypeList();
		}catch(DataAccessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when search Organization!",null);
		}
	}
	
	private void resetFilter() {
		this.categoryTypeFilterLst = null;
		this.filterText = "";
		RequestContext.getCurrentInstance().execute("if (typeof PF('mainCategoryWidgetVar') !== 'undefined') { PF('mainCategoryWidgetVar').clearFilters();}");
	}
	
	/**
	 * Save (new and update) Organization detail
	 * @param event
	 */
	public void  doCategoryDetailSave(ActionEvent event) {
		try {
			
			mainCategoryTypeService.save(FaceContextUtils.getLoginUser(), this.selectedCategoryType);
			this.selectedCategoryType = new MainCategoryType();
			
			doLoadCategoryTypeList();
			setShownDetail(false);
			
			FaceContextUtils.showInfoMessage("Save Successfully!", null);
			
		}catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when saving user profile!",null);
			
		}catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(),null);
		}
	}

	/**
	 * Search Organization by criteria
	 * @param event
	 */
	public void doLoadCategoryTypeList() {
		try{
			setCategoryTypeLst(mainCategoryTypeService.getAllMainCategoryType());
		}catch(DataAccessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when search user!",null);
		}
	}
	
	/**
	 * Delete Organization
	 * @param event
	 */
	public void doCategoryTypeDelete(ActionEvent event) {
		try {
			mainCategoryTypeService.delete(FaceContextUtils.getLoginUser(), this.selectedCategoryType);
			
			doLoadCategoryTypeList();
			this.selectedCategoryType = new MainCategoryType();
			setShownDetail(false);
			
			FaceContextUtils.showInfoMessage("Delete Successfully!", null);
		}catch (DataAccessException e) {	
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when delete user profile!",null);
		} catch (BusinessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
		}
	}
	
	public void doCategoryTypeDetailEdit() {
		setShownDetail(true);
	}
	
	public void doCategoryTypeDetailCancel(){
		this.selectedCategoryType = new MainCategoryType();
		setShownDetail(false);
	}
	
	public void doAddNewCategoryType() {
		setShownDetail(true);
		this.selectedCategoryType = new MainCategoryType();
	}

}
