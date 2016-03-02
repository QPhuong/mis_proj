package com.iso.jaxb.menu;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="menu")
public class MainMenu {

	private List<MenuItem> mainMenuItems;

	public List<MenuItem> getMainMenuItems() {
		return mainMenuItems;
	}
	
	@XmlElementWrapper(name = "mainmenuList")
	@XmlElement(name = "mainmenu")
	public void setMainMenuItems(List<MenuItem> menuItems) {
		this.mainMenuItems = menuItems;
	}
	
	
}
