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

import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.service.UserService;

@ManagedBean(name="userConverter")
@RequestScoped
@FacesConverter(value="userConverter")
public class UserConverter implements Converter{

	@ManagedProperty(value="#{userService}")
	private UserService userService;
	
	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,String value) {
		if(value != null && value.trim().length() > 0) {
			try {
				User user = userService.getUserById(value);
				if (user != null)
					return user;
				return new User();
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
		if (value instanceof User) {
			User user = (User) value;
			return user.getId().toString();
		}
		return "";
	}

}
