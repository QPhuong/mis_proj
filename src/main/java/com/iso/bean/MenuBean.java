package com.iso.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuModel;

import com.iso.domain.User;
import com.iso.jaxb.menu.DefaultMainMenuReader;
import com.iso.jaxb.menu.GroupMenu;
import com.iso.jaxb.menu.MainMenu;
import com.iso.jaxb.menu.MenuItem;
import com.iso.service.UserRoleService;
import com.iso.service.UserService;
import com.iso.util.FaceContextUtils;
import com.iso.util.UserRoleChecker;

@ManagedBean(name="menuBean")
@SessionScoped
public class MenuBean implements Serializable{

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value="#{userRoleService}")
	private UserRoleService userRoleService;
	
	@ManagedProperty(value="#{userService}")
	private UserService userService;
	
	private User loginUser;
	private MainMenu menuStructure;
	
	private MenuModel mainMenu;
	private MenuModel leftMenu;
	private Map<String, MenuModel> leftMenuMap;
	
	private DefaultMenuItem activeMainMenu;
	private DefaultMenuItem activeLeftMenu;
	
	public UserRoleService getUserRoleService() {
		return userRoleService;
	}
	public void setUserRoleService(UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}
	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	public MenuModel getMainMenu() {
		return mainMenu;
	}
	public void setMainMenu(MenuModel mainMenu) {
		this.mainMenu = mainMenu;
	}
	public MenuModel getLeftMenu() {
		return leftMenu;
	}
	public void setLeftMenu(MenuModel leftMenu) {
		this.leftMenu = leftMenu;
	}

	public DefaultMenuItem getActiveMainMenu() {
		return activeMainMenu;
	}
	public void setActiveMainMenu(DefaultMenuItem activeMainMenu) {
		this.activeMainMenu = activeMainMenu;
	}
	public DefaultMenuItem getActiveLeftMenu() {
		return activeLeftMenu;
	}
	public void setActiveLeftMenu(DefaultMenuItem activeLeftMenu) {
		this.activeLeftMenu = activeLeftMenu;
	}
	@PostConstruct
	private void initialize(){
		try {
			//menuStructure = MainMenuGenerator.generateMainMenu();
			loginUser = FaceContextUtils.getLoginUser();
			buildMainMenu();
			doCheckDefaultMainMenu();
		} catch (JAXBException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when loading menu structure", e.getStackTrace().toString());
		} catch (IOException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when loading configuration properties", e.getStackTrace().toString());
		} 
	}
	
	private void doCheckDefaultMainMenu() {
		
		if (CollectionUtils.isNotEmpty(mainMenu.getElements())) {
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String requestURI = request.getRequestURI();
			boolean isPermit = false;
			
			//check permission of request url and active main menu
			for(MenuElement element : this.mainMenu.getElements()) {
				DefaultMenuItem mainMenuItem = (DefaultMenuItem) element;
				String mainMenuItemUrl = mainMenuItem.getParams().get("url").toString().replace("[", "").replace("]", "");
				if (requestURI.contains(mainMenuItemUrl)) {
					this.activeMainMenu = mainMenuItem;
					mainMenuItem.setStyleClass(mainMenuItem.getStyleClass() + " ui-state-active menuitem-active");
					isPermit = true;
				}else {
					mainMenuItem.setStyleClass(mainMenuItem.getStyleClass().replace(" ui-state-active menuitem-active", ""));
				}
			}
			
			if(!isPermit) {
				String menuCode = ((DefaultMenuItem) mainMenu.getElements().get(0)).getParams().get("menuCode").toString();
				menuCode = menuCode.replace("[", "");
				menuCode = menuCode.replace("]", "");
				onClickMainMenu(menuCode);
			}
		}else {
			FaceContextUtils.redirect("/faces/empty.xhtml");
		}
	}
	
	private void buildMainMenu() throws JAXBException, IOException {
		
		MainMenu allMenuStructure;
		allMenuStructure = DefaultMainMenuReader.generateMainMenu();
	
		menuStructure = new MainMenu();
		menuStructure.setMainMenuItems(new ArrayList<MenuItem>());
		
		mainMenu = new DefaultMenuModel();
		leftMenuMap = new HashMap<String, MenuModel>();
		
		if(CollectionUtils.isNotEmpty(allMenuStructure.getMainMenuItems())){
			for(MenuItem item : allMenuStructure.getMainMenuItems()) {
				if(UserRoleChecker.isMenuAccessPermit(loginUser, item)) {
					menuStructure.getMainMenuItems().add(item);
					
					DefaultMenuItem mainMenuItem = new DefaultMenuItem(item.getName());
					mainMenuItem.setId("menuCode_" + item.getCode());
					mainMenuItem.setIcon(item.getIcon());
					mainMenuItem.setCommand("#{menuBean.onClickMainMenu('" + item.getCode() + "')}");
					mainMenuItem.setStyleClass("mainMenu_" + item.getCode());
					
					mainMenuItem.setParam("menuCode", item.getCode());
					
					
					MenuModel leftMenuModel = buildLeftMenu(item, mainMenuItem);
					this.leftMenuMap.put("mainMenu_" + item.getCode(), leftMenuModel);
					
					mainMenu.addElement(mainMenuItem);
				}
			}
		}
	}
	
	private MenuModel buildLeftMenu(MenuItem mainMenu, DefaultMenuItem mainMenuItem) {
		
		leftMenu = new DefaultMenuModel();
		String mainMenuURI = "";
		String defaultLeftMenuCode = "";
		
		if(CollectionUtils.isNotEmpty(mainMenu.getGroups())) {
			for(GroupMenu group : mainMenu.getGroups()) {
				
				DefaultSubMenu submenu = new DefaultSubMenu(group.getName());
				
				if(CollectionUtils.isNotEmpty(group.getMenuItems())) {
					for(MenuItem item : group.getMenuItems()) {
						
						if(UserRoleChecker.isMenuAccessPermit(loginUser, item)) {
							DefaultMenuItem leftMenu = new DefaultMenuItem(item.getName());
							leftMenu.setIcon(item.getIcon());
							leftMenu.setCommand("#{menuBean.onClickLeftMenu('" + item.getCode() + "')}");
							leftMenu.setStyleClass("leftMenu_" + item.getCode());
							//menu param
							leftMenu.setParam("menuCode", item.getCode());
							leftMenu.setParam("url", item.getUrl());
							
							if (StringUtils.isEmpty(mainMenuURI)) {
								mainMenuURI = StringUtils.isEmpty(mainMenuURI) ? item.getUrl() : mainMenuURI;
								defaultLeftMenuCode = item.getCode();
							}
							submenu.addElement(leftMenu);							
						}
					}
				}
				
				if(CollectionUtils.isNotEmpty(submenu.getElements())){
					leftMenu.addElement(submenu);
				}
			}
		}else {
			mainMenuURI = mainMenu.getUrl();
		}
		mainMenuItem.setParam("url", mainMenuURI);
		mainMenuItem.setParam("defaultLeftMenuCode", defaultLeftMenuCode);
		
		return leftMenu;
	}
	
	public void onClickMainMenu(String menuCode) {
		
		String redirectURI = "";
		this.activeMainMenu = null;
		this.activeLeftMenu = null;
		
		//set active main menu and redirect to default left menu if existing
		for(MenuElement element : this.mainMenu.getElements()) {
			
			DefaultMenuItem mainMenuItem = (DefaultMenuItem) element;
			String mainMenuItemCode = mainMenuItem.getParams().get("menuCode").toString().replace("[", "").replace("]", "");
			
			if (mainMenuItemCode.equalsIgnoreCase(menuCode)) {
				this.activeMainMenu = mainMenuItem;
				redirectURI = mainMenuItem.getParams().get("url").toString().replace("[", "").replace("]", "");
				mainMenuItem.setStyleClass(mainMenuItem.getStyleClass() + " ui-state-active menuitem-active");
			}else {
				mainMenuItem.setStyleClass(mainMenuItem.getStyleClass().replace(" ui-state-active menuitem-active", ""));
			}
		}
		
		leftMenu = this.leftMenuMap.get("mainMenu_" + menuCode);

		String defaultLeftMenuCode = this.activeMainMenu.getParams().get("defaultLeftMenuCode").toString().replace("[", "").replace("]", "");
		if(StringUtils.isNotEmpty(defaultLeftMenuCode)) {
			onClickLeftMenu(defaultLeftMenuCode);
		}else {
			FaceContextUtils.redirect(redirectURI);
		}
		
	}
	
	public void onClickLeftMenu(String menuCode) {
		String redirectURI = "";
		
		//set active left menu
		for(MenuElement subElement : this.leftMenu.getElements()) {
			DefaultSubMenu submenu = (DefaultSubMenu) subElement;
			
			for(MenuElement leftElement : submenu.getElements()) {
				DefaultMenuItem leftMenuItem = (DefaultMenuItem) leftElement;
				String leftMenuItemCode = leftMenuItem.getParams().get("menuCode").toString().replace("[", "").replace("]", "");
				
				if (leftMenuItemCode.equalsIgnoreCase(menuCode)) {
					this.setActiveLeftMenu(leftMenuItem);
					redirectURI = leftMenuItem.getParams().get("url").toString().replace("[", "").replace("]", "");
					leftMenuItem.setStyleClass(leftMenuItem.getStyleClass() + " ui-state-active menuitem-active");
				}else {
					leftMenuItem.setStyleClass(leftMenuItem.getStyleClass().replace(" ui-state-active menuitem-active", ""));
				}
			}
		}
		FaceContextUtils.redirect(redirectURI);
	}
}
