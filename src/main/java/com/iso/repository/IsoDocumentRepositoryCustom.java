package com.iso.repository;

import java.io.InputStream;
import java.util.List;

import com.iso.constant.DocumentSearchCriteria;
import com.iso.domain.IsoDocument;
import com.iso.domain.Organization;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

public interface IsoDocumentRepositoryCustom {

	public List<IsoDocument> findByCategory(String categoryId);
	
	public List<IsoDocument> findDeletedDocByCategory(String categoryId);
	
	public List<IsoDocument> findByDocumentSecurityOrganizationUnit(String organizationUnitId, String organizationId);
	
	public List<IsoDocument> findByDocumentSecurityUser(String userId, String organizationId);
	
	public GridFSFile cloneGridFile(IsoDocument document);
	
	public GridFSFile insertGridFile(InputStream inputStream, String filename, String contentType, Organization organization);
	
	public GridFSFile insertGridTemplateFile(InputStream inputStream, String filename, String contentType, Organization organization);
	
	public IsoDocument insertIsoDocument(IsoDocument isoDocument, GridFSFile gridfsFile);
	
	public IsoDocument updateIsoDocument(IsoDocument isoDocument);
	
	public void deleteIsoDocument(IsoDocument isoDocument, boolean permanent);
	
	public GridFSDBFile retrieveGridFSFile(String filedId);
	
	public void restoreIsoDocument(IsoDocument isoDocument);
	
	public long countIsoDocumentsByCategoryId(String id);
	
	public List<IsoDocument> searchText(String organizationId, String searchStr);
	
	public List<IsoDocument> searchByCriteria(DocumentSearchCriteria criteria);
	
	public List<IsoDocument> getListIsoDocumentByListFileID(String organizationId, List<String> fileIds);
	
	public List<IsoDocument> getListIsoDocumentByListID(String organizationId, List<String> isoDocumentIds);
}
