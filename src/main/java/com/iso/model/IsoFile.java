package com.iso.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.io.IOUtils;
import org.primefaces.model.UploadedFile;

public class IsoFile implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String fileName;
	private String path;
	private String contentType;
	private long size;
	private InputStream inputStream;
	private byte[] data;
	
	public IsoFile(){}
	
	public IsoFile(UploadedFile uploadedFile) throws IOException {
		this.fileName = uploadedFile.getFileName();
		this.contentType = uploadedFile.getContentType();
		this.size = uploadedFile.getSize();
		this.data = IOUtils.toByteArray(uploadedFile.getInputstream());
	}
	
	public IsoFile(File file) throws FileNotFoundException, IOException {
		if (file.exists() && !file.isDirectory()) {
			this.fileName = file.getName();
			this.path = file.getAbsolutePath();
			this.data = IOUtils.toByteArray(new FileInputStream(file));
		}
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public InputStream getInputStream() {
		return new ByteArrayInputStream(this.data);
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
}
