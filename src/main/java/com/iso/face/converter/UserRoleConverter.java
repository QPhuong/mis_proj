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

import org.apache.commons.lang3.StringUtils;

import com.iso.domain.Organization;
import com.iso.domain.UserRole;
import com.iso.service.UserRoleService;

@ManagedBean(name="roleConverter")
@ViewScoped
@FacesConverter(value="roleConverter")
public class UserRoleConverter implements Converter{

	@ManagedProperty(value="#{userRoleService}")
	private UserRoleService userRoleService;
	
	private List<UserRole> lstUserRole = new ArrayList<UserRole>();
	
	public List<UserRole> getLstUserRole() {
		return lstUserRole;
	}

	public void setLstUserRole(List<UserRole> lstUserRole) {
		this.lstUserRole = lstUserRole;
	}

	public UserRoleService getUserRoleService() {
		return userRoleService;
	}

	public void setUserRoleService(UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,String value) {
		if(StringUtils.isNotEmpty(value)) {
			try {
				for(UserRole role : lstUserRole) {
					if (value.equals(role.getId().toString())) {
						return role;
					}
				}
				return new UserRole();
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
		if (value instanceof UserRole) {
			UserRole role = (UserRole) value;
			if(!lstUserRole.contains(role)){
				lstUserRole.add(role);
			}
			return role.getId().toString();
		}
		return "";
	}

}
