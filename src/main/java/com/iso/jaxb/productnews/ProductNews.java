package com.iso.jaxb.productnews;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="product")
public class ProductNews {

	private List<ProductNewsItem> newsItems;

	public List<ProductNewsItem> getNewsItems() {
		return newsItems;
	}

	@XmlElementWrapper(name = "verions")
	@XmlElement(name = "version")
	public void setNewsItems(List<ProductNewsItem> newsItems) {
		this.newsItems = newsItems;
	}
	
}
