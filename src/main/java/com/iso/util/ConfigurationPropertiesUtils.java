package com.iso.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.configuration.PropertiesConfiguration;

public final class ConfigurationPropertiesUtils {
	
	public static final String APP_DEMO_MODE = "application_demo_mode";
	
	public static final String APP_CONFIGURATION_REINSTALL = "app_configuration_reinstall";
	
	public static final String MULTI_ORGANIZATION_SETUP = "multi_organization_setup";
	
	public static final String ORGANIZATION_OWNER_CODE = "organization_owner_code";
	public static final String ORGANIZATION_OWNER_NAME = "organization_owner_name";
	
	public static final String CATEGORY_ROOT_NAME = "category_root_name";
	
	public static final String SYS_ADMIN_USERNAME = "sys_admin_username";
	public static final String SYS_ADMIN_FULLNAME = "sys_admin_fullname";
	
	public static final String SYS_ADMIN_ROLE_CODE = "sys_admin_role_code";
	public static final String SYS_ADMIN_ROLE_NAME = "sys_admin_role_name";
	
	public static final String ORG_ADMIN_USERNAME = "org_admin_username";
	public static final String ORG_ADMIN_FULLNAME = "org_admin_fullname";
	
	public static final String ORG_ADMIN_ROLE_CODE = "org_admin_role_code";
	public static final String ORG_ADMIN_ROLE_NAME = "org_admin_role_name";
	
	private static final String PROPERTIIES_FILE_NAME = "configuration.properties";
	
	public static final String DEFAULT_MENU_STRUCTURE_FILE_NAME = "sys_menu_structure_file";
	
	public static final String DEFAULT_MAIN_CATEGORY_FOLDER_PATH = "sys_main_category_folder";
	public static final String DEFAULT_MAIN_CATEGORY_FILE_NAME = "sys_main_category_file";
	
	public static final String PRODUCT_NEWS_FILE_NAME = "product_news_file";
	
	public static final String AUDIT_CHECKLIST_FOLDER = "audit_checklist_folder";
	public static final String AUDIT_CHECKLIST_FILE_EXCEL = "audit_checklist_file_excel";
	public static final String AUDIT_CHECKLIST_FILE_XML = "audit_checklist_file_xml";
	
	private static Properties readPropertiesFile(String propFileName) throws IOException {
		Properties prop = new Properties();
		InputStream inputStream = ConfigurationPropertiesUtils.class.getClassLoader().getResourceAsStream(propFileName);
		
		if (inputStream != null) {
			prop.load(inputStream);
			
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		return prop;
	}
	
	public static String getConfigurationPropertiesValues(String propertyName) throws IOException {
		Properties prop = readPropertiesFile(PROPERTIIES_FILE_NAME);
		return prop.getProperty(propertyName);
	}
	
	public static void setConfigurationProperties(String propertyName, String propertyValue) throws org.apache.commons.configuration.ConfigurationException {
		URL fileURL = ConfigurationPropertiesUtils.class.getClassLoader().getResource(PROPERTIIES_FILE_NAME);
		
		PropertiesConfiguration propConfig = new PropertiesConfiguration(fileURL);
		propConfig.setProperty(propertyName, propertyValue);
		propConfig.save();
		
	}
	
}
