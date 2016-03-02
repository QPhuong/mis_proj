package com.iso.jaxb.productnews;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.iso.util.ConfigurationPropertiesUtils;

public class ProductNewsReader {
	
	public static ProductNews readProductNews() throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ProductNews.class);  
   
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        String filename = ConfigurationPropertiesUtils.getConfigurationPropertiesValues(ConfigurationPropertiesUtils.PRODUCT_NEWS_FILE_NAME);
        InputStream inputStream = ProductNewsReader.class.getClassLoader().getResourceAsStream(filename);
        ProductNews productNews = (ProductNews) jaxbUnmarshaller.unmarshal(inputStream); 
        return productNews;
	}

	public static void main(String[] args) {
		try {
			ProductNews mainmenu = readProductNews();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
