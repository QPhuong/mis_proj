package com.iso.face.converter;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.iso.domain.MainCategoryType;
import com.iso.domain.Organization;
import com.iso.service.MainCategoryTypeService;

@ManagedBean(name="mainCategoryTypeConverter")
@ViewScoped
@FacesConverter(value="mainCategoryTypeConverter")
public class MainCategoryTypeConverter implements Converter{

	@ManagedProperty(value="#{mainCategoryTypeService}")
	private MainCategoryTypeService mainCategoryTypeService;
	
	private List<MainCategoryType> categoryTypeList = new ArrayList<MainCategoryType>();

	public List<MainCategoryType> getCategoryTypeList() {
		return categoryTypeList;
	}

	public void setCategoryTypeList(List<MainCategoryType> categoryTypeList) {
		this.categoryTypeList = categoryTypeList;
	}

	public MainCategoryTypeService getMainCategoryTypeService() {
		return mainCategoryTypeService;
	}

	public void setMainCategoryTypeService(MainCategoryTypeService mainCategoryTypeService) {
		this.mainCategoryTypeService = mainCategoryTypeService;
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,String value) {
		
		if(StringUtils.isNotEmpty(value)) {
			try {
				if(CollectionUtils.isNotEmpty(categoryTypeList)) {
					for(MainCategoryType org : categoryTypeList) {
						if(org.getId().toString().equals(value)) {
							return org;
						}
					}
				}
				return mainCategoryTypeService.getById(value);
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
            }
        }
        else {
        	return new Organization();
        }
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,Object value) {
		if (value instanceof MainCategoryType) {
			MainCategoryType org = (MainCategoryType) value;
			if(!categoryTypeList.contains(org)) {
				categoryTypeList.add(org);
			}
			return org.getId().toString();
		}
		return "";
	}

}
