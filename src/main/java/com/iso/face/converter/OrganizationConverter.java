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

import com.iso.domain.Organization;
import com.iso.service.OrganizationService;
import com.iso.service.impl.OrganizationServiceImpl;

@ManagedBean(name="organizationConverter")
@ViewScoped
@FacesConverter(value="organizationConverter")
public class OrganizationConverter implements Converter{

	@ManagedProperty(value="#{organizationService}")
	private OrganizationService organizationService;
	
	private List<Organization> orgList = new ArrayList<Organization>();
	
	public List<Organization> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<Organization> orgList) {
		this.orgList = orgList;
	}

	public OrganizationService getOrganizationService() {
		return organizationService;
	}

	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,String value) {
		
		if(StringUtils.isNotEmpty(value)) {
			try {
				if(CollectionUtils.isNotEmpty(orgList)) {
					for(Organization org : orgList) {
						if(org.getId().toString().equals(value)) {
							return org;
						}
					}
				}
				return organizationService.getOrganizationById(value);
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
		if (value instanceof Organization) {
			Organization org = (Organization) value;
			if(!orgList.contains(org)) {
				orgList.add(org);
			}
			return org.getId().toString();
		}
		return "";
	}

}
