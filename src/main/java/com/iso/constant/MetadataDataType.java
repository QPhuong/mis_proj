package com.iso.constant;

import java.awt.List;
import java.sql.Date;

public enum MetadataDataType {
	STRING(String.class), INT(Integer.class), LONG(Long.class), DOUBLE(Double.class), BOOLEAN(Boolean.class), LIST(List.class), DATE(Date.class);
	
	private Class<?> clazz;
	
	private MetadataDataType(Class<?> clazz){
		this.clazz = clazz;
	}
	
	public Class<?> toClass() {
		return this.clazz;
	}
}
