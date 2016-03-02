package com.iso.jaxb.productnews;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class ProductNewsItem implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String version;
	private String message;
	private Date releasedDate;
	private String author;
	private List<String> newfeatures;
	private List<String> improvedfeatures;
	
	public String getVersion() {
		return version;
	}
	@XmlElement
	public void setVersion(String version) {
		this.version = version;
	}
	public String getMessage() {
		return message;
	}
	@XmlElement
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getReleasedDate() {
		return releasedDate;
	}
	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	public void setReleasedDate(Date releasedDate) {
		this.releasedDate = releasedDate;
	}
	public String getAuthor() {
		return author;
	}
	@XmlElement
	public void setAuthor(String author) {
		this.author = author;
	}
	public List<String> getNewfeatures() {
		return newfeatures;
	}
	@XmlElementWrapper(name = "newfeatures")
	@XmlElement(name = "feature")
	public void setNewfeatures(List<String> newfeatures) {
		this.newfeatures = newfeatures;
	}
	public List<String> getImprovedfeatures() {
		return improvedfeatures;
	}
	@XmlElementWrapper(name = "improvefeatures")
	@XmlElement(name = "feature")
	public void setImprovedfeatures(List<String> improvedfeatures) {
		this.improvedfeatures = improvedfeatures;
	}
	
}
