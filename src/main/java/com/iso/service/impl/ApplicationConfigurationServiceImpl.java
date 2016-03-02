package com.iso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.iso.domain.ApplicationConfiguration;
import com.iso.domain.Menu;
import com.iso.domain.UserRole;
import com.iso.jaxb.menu.DefaultMainMenuReader;
import com.iso.jaxb.menu.MainMenu;
import com.iso.repository.ApplicationConfigurationRepository;
import com.iso.repository.CategoryRepository;
import com.iso.repository.OrganizationRepository;
import com.iso.repository.UserRepository;
import com.iso.repository.UserRoleRepository;
import com.iso.service.ApplicationConfigurationService;
import com.iso.util.FaceContextUtils;

@Component(value="applicationConfigurationService")
public class ApplicationConfigurationServiceImpl implements ApplicationConfigurationService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private ApplicationConfigurationRepository appConfigRepo;
	@Autowired
	private OrganizationRepository organizationRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private UserRoleRepository userRoleRepo;
	@Autowired
	private CategoryRepository categoryRepo;
	
	
	private ApplicationConfiguration saveOrganization(ApplicationConfiguration config){
		config.getOrgOwner().setActive(true);
		config.getOrgOwner().setLocked(false);
		config.getOrgOwner().setOwnerOrganization(true);
		
		config.setOrgOwner(organizationRepo.save(config.getOrgOwner()));
		config.getLogs().add("Add owner organization information successfully");
		return config;
	}
	
	private ApplicationConfiguration saveSysUserRole(ApplicationConfiguration config) {
		
		try { 
			config.getSysUserRole().setCreatedOn(Calendar.getInstance().getTime());
			config.getSysUserRole().setOrganization(config.getOrgOwner());
			config.getSysUserRole().setSystemAdminRole(true);
			config.getSysUserRole().setOrganizationAdminRole(true);
			
			//set all menu permission
			MainMenu mainMenu = DefaultMainMenuReader.generateMainMenu();
			List<Menu> menuLst = DefaultMainMenuReader.buildMenu(mainMenu, true);
			
			/*List<Menu> menuLst = new ArrayList<Menu>();
			MainMenu mainMenu = DefaultMainMenuSupporter.generateMainMenu();
			
			if(CollectionUtils.isNotEmpty(mainMenu.getMainMenuItems())) {
				for(MenuItem mainMenuItem : mainMenu.getMainMenuItems()) {
					
					menuLst.add(new Menu(mainMenuItem.getId(), mainMenuItem.getCode(), mainMenuItem.getName()));
					
					if(CollectionUtils.isNotEmpty(mainMenuItem.getGroups())) {
						for(GroupMenu group : mainMenuItem.getGroups()) {
							
							if(CollectionUtils.isNotEmpty(group.getMenuItems())) {
								for(MenuItem leftMenuItem : group.getMenuItems()){
									menuLst.add(new Menu(leftMenuItem.getId(), leftMenuItem.getCode(), leftMenuItem.getName()));
								}
							}
						}
					}
				}
			}*/
			
			config.getSysUserRole().setMenuLst(menuLst);
			config.setSysUserRole(userRoleRepo.save(config.getSysUserRole()));
			
		} catch (JAXBException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when read menu information: " + e.getMessage(), e.getStackTrace().toString());
		} catch (IOException e) {
			e.printStackTrace();
			FaceContextUtils.showErrorMessage("Error when read menu information: " + e.getMessage(), e.getStackTrace().toString());
		}
		return config;
	}

	private ApplicationConfiguration saveSysAdminUser(ApplicationConfiguration config){
		config.getSysAdmin().setOrganization(config.getOrgOwner());
		config.getSysAdmin().setPassword(config.getSysAdmin().getPassword());
		config.getSysAdmin().setCreatedOn(Calendar.getInstance().getTime());
		config.getSysAdmin().setUserRoles(new ArrayList<UserRole>());
		config.getSysAdmin().getUserRoles().add(config.getSysUserRole());
		config.getSysAdmin().setActive(true);
		
		config.setSysAdmin(userRepo.save(config.getSysAdmin()));
		config.getLogs().add("Add system administration information successfully");
		return config;
	}
	
	/*private ApplicationConfiguration saveCategory(ApplicationConfiguration config){
		config.getRootCategory().setCreatedBy(null);
		config.getRootCategory().setCreatedOn(Calendar.getInstance().getTime());
		config.getRootCategory().setLocked(false);
		config.getRootCategory().setParent(null);
		
		config.setRootCategory(categoryRepo.save(config.getRootCategory()));
		config.getLogs().add("Add root category of owner organization successfully");
		
		config.getOrgOwner().setRootCategory(config.getRootCategory());
		config.setOrgOwner(organizationRepo.save(config.getOrgOwner()));
		config.getLogs().add("Set root category to owner organization successfully");
		
		return config;
	}
	
	private ApplicationConfiguration saveCategorySecurity(ApplicationConfiguration config) {
		List<CategorySecurity> securities = new ArrayList<CategorySecurity>();
		CategorySecurity defaultSecurity = new CategorySecurity(true);
		defaultSecurity.setUser(config.getSysAdmin());
		securities.add(defaultSecurity);
		config.getRootCategory().setCategorySecurities(securities);
		
		config.setRootCategory(categoryRepo.save(config.getRootCategory()));
		config.getLogs().add("Add root category security successfully");
		return config;
	}*/
	
	private ApplicationConfiguration saveApplicationConfiguration(ApplicationConfiguration config) {
		config.setInstallationCompleted(true);
		config.getLogs().add("Add new application configuration successfully");
		return appConfigRepo.save(config);
	}	
	
	public ApplicationConfiguration save(ApplicationConfiguration config) {
		config.setLogs(new ArrayList<String>());
		config = this.saveOrganization(config);
		//config = this.saveCategory(config);
		config = this.saveSysUserRole(config);
		config = this.saveSysAdminUser(config);
		//config = this.saveCategorySecurity(config);
		config = this.saveApplicationConfiguration(config);
		return config;
	}
	
	public ApplicationConfiguration getLatestConfiguration() {
		return appConfigRepo.getLatestConfiguration();
	}
	
	@SuppressWarnings("unchecked")
	public List<ApplicationConfiguration> getAllConfiguration() {
		return IteratorUtils.toList(appConfigRepo.findAll(new Sort(Sort.Direction.DESC, "createdOn")).iterator());
	}
}
