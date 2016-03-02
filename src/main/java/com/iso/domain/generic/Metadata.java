package com.iso.domain.generic;

import java.io.Serializable;
import java.util.Date;

import com.iso.constant.MetadataControlType;
import com.iso.constant.MetadataDataType;

public class Metadata<T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private T value;
	private MetadataControlType controlType;
	private MetadataDataType datatype;
	private boolean isRequired;
	private boolean isEditable;

	public static Metadata<?> newInstance(String name, MetadataControlType controlType) {
		if (controlType.equals(MetadataControlType.INPUT)) {
			return new Metadata<String>(name, controlType, MetadataDataType.STRING);
			
		}else if (controlType.equals(MetadataControlType.BOOLEAN)) {
			return new Metadata<Boolean>(name, controlType, MetadataDataType.BOOLEAN);
			
		}else if (controlType.equals(MetadataControlType.DATE)) {
			return new Metadata<Date>(name, controlType, MetadataDataType.DATE);
			
		}else if (controlType.equals(MetadataControlType.DATETIME)) {
			return new Metadata<Date>(name, controlType, MetadataDataType.DATE);
			
		}else if (controlType.equals(MetadataControlType.TEXTAREA)) {
			return new Metadata<String>(name, controlType, MetadataDataType.STRING);
			
		}else if (controlType.equals(MetadataControlType.NUMBER)) {
			return new Metadata<Double>(name, controlType, MetadataDataType.DOUBLE);
		}
		return new Metadata<Object>();
	}
	
	public Metadata(){}
	
	private Metadata(String name, MetadataControlType controlType, MetadataDataType datatype) {
		this.name = name;
		this.controlType = controlType;
		this.datatype = datatype;
	}

	public MetadataDataType getDatatype() {
		return datatype;
	}
	public void setDatatype(MetadataDataType datatype) {
		this.datatype = datatype;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public MetadataControlType getControlType() {
		return controlType;
	}
	public void setControlType(MetadataControlType type) {
		this.controlType = type;
	}
	public boolean isRequired() {
		return isRequired;
	}
	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}
	public boolean isEditable() {
		return isEditable;
	}
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}
}
