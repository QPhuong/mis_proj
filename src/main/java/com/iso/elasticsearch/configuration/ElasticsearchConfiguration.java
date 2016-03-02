package com.iso.elasticsearch.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.configuration.PropertiesConfiguration;

public final class ElasticsearchConfiguration {
	
	public static final String ELASTICSEARCH_ACTIVE = "elasticsearch_active";
	
	public static final String ELASTICSEARCH_HOST = "host";
	
	public static final String ELASTICSEARCH_PORT = "port";
	
	public static final String ELASTICSEARCH_ISO_DOCUMENT_INDEX_NAME = "isodocument_index_name";
	
	public static final String ELASTICSEARCH_ISO_FILE_INDEX_NAME = "isofile_index_name";
	
	private static final String PROPERTIIES_FILE_NAME = "elasticsearch.properties";
	
	private static Properties readPropertiesFile(String propFileName) throws IOException {
		Properties prop = new Properties();
		InputStream inputStream = ElasticsearchConfiguration.class.getClassLoader().getResourceAsStream(propFileName);
		
		if (inputStream != null) {
			prop.load(inputStream);
			
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		return prop;
	}
	
	public static String getPropertiesValues(String propertyName) throws IOException {
		Properties prop = readPropertiesFile(PROPERTIIES_FILE_NAME);
		return prop.getProperty(propertyName);
	}
	
	public static void setProperties(String propertyName, String propertyValue) throws org.apache.commons.configuration.ConfigurationException {
		URL fileURL = ElasticsearchConfiguration.class.getClassLoader().getResource(PROPERTIIES_FILE_NAME);
		
		PropertiesConfiguration propConfig = new PropertiesConfiguration(fileURL);
		propConfig.setProperty(propertyName, propertyValue);
		propConfig.save();
		
	}
	
}
