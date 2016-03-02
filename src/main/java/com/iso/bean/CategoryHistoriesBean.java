package com.iso.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.springframework.dao.DataAccessException;

import com.iso.domain.Category;
import com.iso.domain.UserActivity;
import com.iso.service.CategoryService;
import com.iso.service.UserActivityService;
import com.iso.util.FaceContextUtils;

@ManagedBean(name="categoryHistoriesBean")
@ViewScoped
public class CategoryHistoriesBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private Category selectedCat;
	private List<UserActivity> userActivities;
	
	@ManagedProperty(value="#{categoryService}")
	private CategoryService categoryService;
	
	@ManagedProperty(value="#{userActivityService}")
	private UserActivityService userActivityService;
	
	public Category getSelectedCat() {
		return selectedCat;
	}
	public void setSelectedCat(Category selectedCat) {
		this.selectedCat = selectedCat;
	}
	public CategoryService getCategoryService() {
		return categoryService;
	}
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	public List<UserActivity> getUserActivities() {
		return userActivities;
	}
	public void setUserActivities(List<UserActivity> userActivities) {
		this.userActivities = userActivities;
	}
	public UserActivityService getUserActivityService() {
		return userActivityService;
	}
	public void setUserActivityService(UserActivityService userActivityService) {
		this.userActivityService = userActivityService;
	}

	@PostConstruct
	public void initialize() {
		try{
			doLoadCategoryHistories();
		}catch(DataAccessException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when loading category histories!", null);
		}
	}
	
	public void doLoadCategoryHistories() {
		Object bean = FaceContextUtils.getViewScopeBean("categoryTreeBean");
		if (bean != null) {
			CategoryTreeBean catBean = (CategoryTreeBean) bean;
			this.selectedCat = catBean.getSelectedCategory();
			if (this.selectedCat != null && this.selectedCat.getId() != null) {
				this.userActivities = userActivityService.getDomainHistories(this.selectedCat);
			}
		}else {
			FaceContextUtils.showErrorMessage("Error when loading category histories!", null);
		}
	}
	
}
