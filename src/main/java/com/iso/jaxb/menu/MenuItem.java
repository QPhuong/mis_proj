package com.iso.jaxb.menu;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MenuItem {

	private String code;
	
	private String name;
	
	private List<GroupMenu> groups;

	private String url;
	
	private String icon;
	
	private boolean security;
	
	public MenuItem(){}
	
	public MenuItem(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	@XmlElement
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	
	@XmlElement
	public void setName(String name) {
		this.name = name;
	}
	
	public List<GroupMenu> getGroups() {
		return groups;
	}
	
	@XmlElementWrapper(name = "groups")
	@XmlElement(name = "group")
	public void setGroups(List<GroupMenu> groups) {
		this.groups = groups;
	}

	public String getUrl() {
		return url;
	}
	@XmlElement
	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}
	@XmlElement
	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isSecurity() {
		return security;
	}
	@XmlElement(defaultValue="false")
	public void setSecurity(boolean security) {
		this.security = security;
	}
	
	
}
