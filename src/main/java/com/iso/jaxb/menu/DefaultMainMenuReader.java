package com.iso.jaxb.menu;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections.CollectionUtils;

import com.iso.domain.Menu;
import com.iso.util.ConfigurationPropertiesUtils;

public class DefaultMainMenuReader {
	
	public static MainMenu generateMainMenu() throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(MainMenu.class);  
   
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        String filename = ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.DEFAULT_MENU_STRUCTURE_FILE_NAME);
        InputStream inputStream = DefaultMainMenuReader.class.getClassLoader().getResourceAsStream(filename);
        MainMenu mainmenu = (MainMenu) jaxbUnmarshaller.unmarshal(inputStream); 
        return mainmenu;
	}

	public static List<Menu> buildMenu(MainMenu mainMenu, boolean isSystemRole) {
		List<Menu> menuLst = new ArrayList<Menu>();
		
		if(CollectionUtils.isNotEmpty(mainMenu.getMainMenuItems())) {
			for(MenuItem menuItem : mainMenu.getMainMenuItems()) {
				
				if (menuItem.isSecurity()) {
					if (isSystemRole) {
						menuLst.add(new Menu(menuItem.getCode(), menuItem.getName()));
					}
				}else {
					menuLst.add(new Menu(menuItem.getCode(), menuItem.getName()));
				}
				
				if(CollectionUtils.isNotEmpty(menuItem.getGroups())) {
					for(GroupMenu group : menuItem.getGroups()) {
						
						if(CollectionUtils.isNotEmpty(group.getMenuItems())) {
							for(MenuItem subMenuItem : group.getMenuItems()){
								menuLst.add(new Menu(subMenuItem.getCode(), subMenuItem.getName()));
							}
						}
					}
				}
			}
		}
		
		return menuLst;
	}
	
	/*public static void main(String[] args) {
		try {
			MainMenu mainmenu = generateMainMenu();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
