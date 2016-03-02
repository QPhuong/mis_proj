package com.iso.face.converter;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.iso.domain.Organization;
import com.iso.domain.generic.Property;

@ManagedBean(name="propertyConverter")
@ViewScoped
@FacesConverter(value="propertyConverter")
public class PropertyConverter implements Converter{

	private List<Property<?>> properties = new ArrayList<Property<?>>();
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,String value) {
		
		if(StringUtils.isNotEmpty(value)) {
			try {
				if(CollectionUtils.isNotEmpty(properties)) {
					for(Property<?> prop : properties) {
						if(prop.getKey().equals(value)) {
							return prop;
						}
					}
				}
				return null;
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid value."));
            }
        }
        else {
        	return new Organization();
        }
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,Object value) {
		if (value instanceof Property<?>) {
			Property<?> prop = (Property<?>) value;
			if(!properties.contains(prop)) {
				properties.add(prop);
			}
			return prop.getKey();
		}
		return "";
	}

}
