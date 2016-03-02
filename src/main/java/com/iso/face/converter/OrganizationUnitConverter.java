package com.iso.face.converter;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.iso.domain.OrganizationUnit;
import com.iso.service.OrganizationUnitService;

@ManagedBean(name="organizationUnitConverter")
@RequestScoped
@FacesConverter(value="organizationUnitConverter")
public class OrganizationUnitConverter implements Converter{

	@ManagedProperty(value="#{organizationUnitService}")
	private OrganizationUnitService organizationUnitService;

	public OrganizationUnitService getOrganizationUnitService() {
		return organizationUnitService;
	}
	public void setOrganizationUnitService(OrganizationUnitService organizationUnitService) {
		this.organizationUnitService = organizationUnitService;
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,String value) {
		
		if(value != null && value.trim().length() > 0) {
			try {
				OrganizationUnit orgUnit = organizationUnitService.getOrganizationUnitById(value);
				if (orgUnit != null)
					return orgUnit;
				return new OrganizationUnit();
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid theme."));
            }
        }
        else {
        	return new OrganizationUnit();
        }
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,Object value) {
		if (value instanceof OrganizationUnit) {
			OrganizationUnit orgUnit = (OrganizationUnit) value;
			return orgUnit.getId().toString();
		}
		return "";
	}

}
