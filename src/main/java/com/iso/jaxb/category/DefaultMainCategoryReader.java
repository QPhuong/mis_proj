package com.iso.jaxb.category;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.iso.util.ConfigurationPropertiesUtils;

public class DefaultMainCategoryReader {

	public static DefaultMainCategory generateMainCategory() throws JAXBException, IOException{
		JAXBContext jaxbContext = JAXBContext.newInstance(DefaultMainCategory.class);  
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        
        InputStream inputStream;
        DefaultMainCategory mainCategory;
        
        String filename = ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.DEFAULT_MAIN_CATEGORY_FILE_NAME);
        String folderName = ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.DEFAULT_MAIN_CATEGORY_FOLDER_PATH);
        
        String path = filename;
        
        if (StringUtils.isNotEmpty(folderName)) {
        	path = StringUtils.trim(folderName) + File.separator + StringUtils.trim(filename);
        }

        String folderAbsolutePath = DefaultMainCategoryReader.class.getClassLoader().getResource("").getPath();
        if (folderAbsolutePath.endsWith(File.separator) || folderAbsolutePath.endsWith("/")) {
        	folderAbsolutePath += StringUtils.trim(folderName);
        }else {
        	folderAbsolutePath += File.separator + StringUtils.trim(folderName);
        }
        
        
        inputStream = DefaultMainCategoryReader.class.getClassLoader().getResourceAsStream(path);
    	mainCategory = (DefaultMainCategory) jaxbUnmarshaller.unmarshal(inputStream); 
                
    	getFileTemplates(mainCategory, folderAbsolutePath);
    	
        return mainCategory;
	}
	
	
	private static void getFileTemplates(DefaultMainCategory mainCategory, String folderPath) throws IOException {
		
		folderPath = StringUtils.trim(folderPath) + File.separator + StringUtils.trim(mainCategory.getName().replace(":", "_"));
		
		List<File> templates = new ArrayList<File>();
		File folder = new File(folderPath);
		
		if(folder.exists() && folder.isDirectory()) {
			File[] listOfFiles = folder.listFiles();
			for(File file : listOfFiles) {
				if(file.isFile()) {
					templates.add(file);
				}
			}
		}
		
		mainCategory.setFileTemplates(templates);
		
		if (CollectionUtils.isNotEmpty(mainCategory.getSubcategories())) {
			for (DefaultMainCategory subCategory : mainCategory.getSubcategories()) {
				getFileTemplates(subCategory, folderPath);
			}
		}
	}
	
	/*public static void main(String[] args) {
		try {
			DefaultMainCategory maincategories = generateMainCategory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
