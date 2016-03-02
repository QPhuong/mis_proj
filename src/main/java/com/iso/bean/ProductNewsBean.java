package com.iso.bean;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.bind.JAXBException;

import com.iso.jaxb.productnews.ProductNews;
import com.iso.jaxb.productnews.ProductNewsReader;
import com.iso.util.FaceContextUtils;

@ManagedBean
@ViewScoped
public class ProductNewsBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private ProductNews productNews;
	
	public ProductNews getProductNews() {
		return productNews;
	}

	public void setProductNews(ProductNews productNews) {
		this.productNews = productNews;
	}

	@PostConstruct
	private void initialize() {
		try {
			productNews = ProductNewsReader.readProductNews();
		} catch (JAXBException e) {
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
			e.printStackTrace();
		} catch (IOException e) {
			FaceContextUtils.showErrorMessage(e.getMessage(), e.getStackTrace().toString());
			e.printStackTrace();
		}
	}
}
