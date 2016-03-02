package com.iso.domain.generic;

import java.io.Serializable;

public class Property<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private String key;
	private T value;
	
	public Property(){}
	
	public Property(String key, T value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	
}
