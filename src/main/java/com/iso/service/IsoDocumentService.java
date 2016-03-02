package com.iso.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iso.constant.DocumentSearchCriteria;
import com.iso.domain.Category;
import com.iso.domain.IsoDocument;
import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.exception.BusinessException;
import com.iso.model.IsoFile;
import com.mongodb.gridfs.GridFSDBFile;

@Service
public interface IsoDocumentService extends Serializable{

	public void checkedInNewFileVersion(User loginUser, IsoDocument isoDocument, IsoFile uploadFile, boolean isMajorVersion, String comment) throws IOException;
	
	public IsoDocument uploadNewFile(User loginUser, IsoFile uploadFile, Category category) throws IOException, BusinessException;
	
	public IsoDocument saveDocumentExtProperties(User loginUser, IsoDocument file, String comment);
	
	public IsoDocument saveDocumentProperties(User loginUser, IsoDocument file, String comment);
	
	public IsoDocument saveDocumentSecurity(User loginUser, IsoDocument document);
	
	public IsoDocument saveDocumentNote(User loginUser, IsoDocument document, String activity);
	
	public IsoDocument lockedIsoDocument(User loginUser, IsoDocument document);
	
	public IsoDocument unlockedIsoDocument(User loginUser, IsoDocument document);
	
	public IsoDocument checkedOutIsoDocument(User loginUser, IsoDocument document);
	
	public void deleteFilePermanent(User loginUser, IsoDocument file);
	
	public void deleteFile(User loginUser, IsoDocument file);
	
	public List<IsoDocument> getFilesByCateogry(Category category);
	
	public List<IsoDocument> getDeletedFileByCategory(Category category);
	
	public GridFSDBFile getGridFSFile(User loginUser, String isoDocumentId);
	
	public GridFSDBFile getGridFSFile(User loginUser, IsoDocument isoDocument);
	
	public long countFilesByCategory(Category category);
	
	public IsoDocument getIsoDocumentAndSecurityById(String id, User accessUser);
	
	public List<IsoDocument> searchText(Organization organization, String searchText);
	
	public List<IsoDocument> searchByCriteria(Organization organization, DocumentSearchCriteria criteria);
	
	public void restoreDocument(User loginUser, IsoDocument file) throws BusinessException;
	
	public void restoreCategoryAndDocument(User loginUser, IsoDocument file) throws BusinessException;
	
}
