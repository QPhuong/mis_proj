package com.iso.domain.generic;

import java.io.Serializable;

public class DomainObjectVersion<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private String clazz;
	private String version;
	private T original;
	private T newest;
	
	public DomainObjectVersion(){}
	
	public DomainObjectVersion(Class<?> clazz, T original, T newest){
		this.clazz = clazz.getName();
		this.original = original;
		this.newest = newest;
	}
	
	public DomainObjectVersion(Class<?> clazz, String version, T original, T newest){
		this.clazz = clazz.getName();
		this.version = version;
		this.original = original;
		this.newest = newest;
	}
	
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public T getOriginal() {
		return original;
	}
	public void setOriginal(T original) {
		this.original = original;
	}
	public T getNewest() {
		return newest;
	}
	public void setNewest(T newest) {
		this.newest = newest;
	}
	
	
}
