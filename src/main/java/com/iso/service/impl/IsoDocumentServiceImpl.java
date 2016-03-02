package com.iso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.iso.constant.AdministratorActivityLogs;
import com.iso.constant.DocumentActivityLogs;
import com.iso.constant.DocumentSearchCriteria;
import com.iso.constant.LogTypes;
import com.iso.domain.Category;
import com.iso.domain.CategorySecurity;
import com.iso.domain.DocumentSecurity;
import com.iso.domain.DocumentVersion;
import com.iso.domain.IsoDocument;
import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.domain.UserActivity;
import com.iso.domain.generic.DomainObjectVersion;
import com.iso.exception.BusinessException;
import com.iso.model.IsoFile;
import com.iso.repository.DocumentVersionRepository;
import com.iso.repository.IsoDocumentRepository;
import com.iso.service.CategoryService;
import com.iso.service.IsoDocumentService;
import com.iso.service.OrganizationService;
import com.iso.service.UserActivityService;
import com.iso.util.DomainSecurityChecker;
import com.iso.util.DomainVersionGenerator;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

@Component(value="isoDocumentService")
public class IsoDocumentServiceImpl implements IsoDocumentService{	
	
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private IsoDocumentRepository isoDocumentRepository;
	
	@Autowired
	private DocumentVersionRepository docVersionRepo;
	
	@Autowired
	@Qualifier(value="organizationService")
	OrganizationService organizationService;
	
	@Autowired
	@Qualifier(value="categoryService")
	private CategoryService categoryService;
	
	@Autowired
	@Qualifier(value="userActivityService")
	private UserActivityService userActivityService;
	
	
	public void checkedInNewFileVersion(User loginUser, IsoDocument isoDocument, IsoFile uploadFile, boolean isMajorVersion, String comment) throws IOException {
		
		IsoDocument original = isoDocumentRepository.findOne(isoDocument.getId().toString());
		isoDocument = isoDocumentRepository.findOne(isoDocument.getId().toString());
		isoDocument.setCheckedOut(false);
		
		//insert gridFS File
		GridFSFile gridfsFile = isoDocumentRepository.insertGridFile(uploadFile.getInputStream(), uploadFile.getFileName(), uploadFile.getContentType(), loginUser.getOrganization());
		
		//save iso file
		String newVersionStr = DomainVersionGenerator.generateNewVerion(isoDocument.getLatestVersion(), isMajorVersion);
		isoDocument.setFileId((ObjectId)gridfsFile.getId());
		isoDocument.setExtension(FilenameUtils.getExtension(gridfsFile.getFilename()).toLowerCase());
		isoDocument.setFileTitle(FilenameUtils.getBaseName(gridfsFile.getFilename()));
		isoDocument.setContentType(gridfsFile.getContentType());
		isoDocument.setSize(gridfsFile.getLength());
		isoDocument.setLatestVersion(newVersionStr);
		isoDocument.setUpdatedBy(loginUser);
		isoDocument.setUpdatedOn(Calendar.getInstance().getTime());
		isoDocument.setCheckedOut(false);
		isoDocument.setLockedBy(null);
		isoDocument = isoDocumentRepository.save(isoDocument);
		
		//insert doc version
		DocumentVersion version = new DocumentVersion(loginUser, isoDocument);
		version.setComment(comment);
		version.setMajorVersion(isMajorVersion);
		version.setVersion(newVersionStr);
		version.setVersionDate(isoDocument.getUpdatedOn());
		docVersionRepo.save(version);
		
		//inser activity log
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.DOCUMENT);
		logActivity.setActiviy(DocumentActivityLogs.CHECK_IN_DOCUMENT);
		logActivity.setDomainRef(isoDocument);
		logActivity.setDomainVersion(new DomainObjectVersion<IsoDocument>(IsoDocument.class, original, isoDocument));
		logActivity.setVersion(isoDocument.getLatestVersion());
		userActivityService.save(logActivity);
		
	}
	
	public IsoDocument uploadNewFile(User loginUser, IsoFile uploadFile, Category category) throws IOException, BusinessException {
		
		//insert gridFS File and IsoDocument
		GridFSFile gridfsFile = isoDocumentRepository.insertGridFile(uploadFile.getInputStream(), uploadFile.getFileName(), uploadFile.getContentType(), loginUser.getOrganization());
		
		//insert iso document
		IsoDocument file = new IsoDocument();	
		file.setOrganization(loginUser.getOrganization());
		file.setLatestVersion(DomainVersionGenerator.generateNewVerion("", true));
		file.setCategory(category);
		file.setCreatedBy(loginUser);
		file.setCreatedOn(Calendar.getInstance().getTime());
		
		//set default security
		if (CollectionUtils.isNotEmpty(category.getCategorySecurities())) {
			
			List<DocumentSecurity> lstDocSecurity = new ArrayList<DocumentSecurity>();
			
			for (CategorySecurity categorySecurity : category.getCategorySecurities()) {
				DocumentSecurity documentSecurity = new DocumentSecurity(true);
				documentSecurity.setOrgUnit(categorySecurity.getOrgUnit());
				documentSecurity.setUser(categorySecurity.getUser());
				lstDocSecurity.add(documentSecurity);
			}
			file.setDocumentSecurities(lstDocSecurity);
		}
		
		file = isoDocumentRepository.insertIsoDocument(file, gridfsFile);
		
		//insert doc version
		DocumentVersion version = new DocumentVersion(loginUser, file);
		version.setComment("");
		version.setMajorVersion(true);
		version.setVersion(file.getLatestVersion());
		version.setVersionDate(file.getCreatedOn());
		docVersionRepo.save(version);
		
		//lock the organization
		if (loginUser.getOrganization() != null && loginUser.getOrganization().getId() != null ) {
			Organization org = organizationService.getOrganizationById(loginUser.getOrganization().getId().toString());
			if (!org.isLocked()) {
				org.setLocked(true);
				org = organizationService.saveOrganizationData(loginUser, org, AdministratorActivityLogs.ORGANIZATION_LOCKED);
				loginUser.setOrganization(org);
			}
		}
		
		//insert activity log
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.DOCUMENT);
		logActivity.setActiviy(DocumentActivityLogs.UPLOAD_NEW_DOC);
		logActivity.setDomainRef(file);
		logActivity.setDomainVersion(new DomainObjectVersion<IsoDocument>(IsoDocument.class, null, file));
		logActivity.setVersion(file.getLatestVersion());
		userActivityService.save(logActivity);
		
		return file;
	}
	
	private IsoDocument saveIsoDocument(User loginUser, IsoDocument file, String log, String comment) {
		IsoDocument original = isoDocumentRepository.findOne(file.getId().toString());
		
		file.setUpdatedBy(loginUser);
		file.setUpdatedOn(Calendar.getInstance().getTime());
		file = isoDocumentRepository.save(file);
		
		//inser activity log
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.DOCUMENT);
		logActivity.setActiviy(log);
		logActivity.setDomainRef(file);
		logActivity.setDomainVersion(new DomainObjectVersion<IsoDocument>(IsoDocument.class, original, file));
		logActivity.setComment(comment);
		logActivity.setVersion(file.getLatestVersion());
		userActivityService.save(logActivity);
		
		return file;
	}
	
	@Override
	public IsoDocument saveDocumentExtProperties(User loginUser, IsoDocument file, String comment) {
		return saveIsoDocument(loginUser, file, DocumentActivityLogs.MODIFY_DOC_EXT_PROPERTIES, comment);
	}
	
	@Override
	public IsoDocument saveDocumentProperties(User loginUser, IsoDocument file, String comment) {
		return saveIsoDocument(loginUser, file, DocumentActivityLogs.MODIFY_DOC_PROPERTIES, comment);
	}
	
	@Override
	public IsoDocument saveDocumentSecurity(User loginUser, IsoDocument document) {
		return saveIsoDocument(loginUser, document, DocumentActivityLogs.SETUP_DOC_SECURITY, null);
	}
	
	@Override
	public IsoDocument saveDocumentNote(User loginUser, IsoDocument document, String activity) {
		return saveIsoDocument(loginUser, document, activity, null);
	}
	
	public IsoDocument lockedIsoDocument(User loginUser, IsoDocument document) {
		document = isoDocumentRepository.findOne(document.getId().toString());
		document.setLocked(true);
		document.setLockedBy(loginUser);
		return saveIsoDocument(loginUser, document, DocumentActivityLogs.LOCK_DOCUMENT, "");
	}
	
	public IsoDocument unlockedIsoDocument(User loginUser, IsoDocument document) {
		document = isoDocumentRepository.findOne(document.getId().toString());
		document.setCheckedOut(false);
		document.setLocked(false);
		document.setLockedBy(null);
		return saveIsoDocument(loginUser, document, DocumentActivityLogs.UNLOCK_DOCUMENT, "");
	}
	
	public IsoDocument checkedOutIsoDocument(User loginUser, IsoDocument document) {
		document = isoDocumentRepository.findOne(document.getId().toString());
		document.setCheckedOut(true);
		document.setLockedBy(loginUser);
		return saveIsoDocument(loginUser, document, DocumentActivityLogs.CHECK_OUT_DOCUMENT, "");
	}
	
	public void deleteFilePermanent(User loginUser, IsoDocument file) {
		deleteFile(loginUser, file, true);
	}
	
	public void deleteFile(User loginUser, IsoDocument file) {
		deleteFile(loginUser, file, false);
	}
	
	private void deleteFile(User loginUser, IsoDocument file, boolean permanent) {
		
		IsoDocument original = isoDocumentRepository.findOne(file.getId().toString());
		
		file.setDeletedBy(loginUser);
		file.setDeletedOn(Calendar.getInstance().getTime());
		file.setDeleted(true);
		
		isoDocumentRepository.deleteIsoDocument(file, permanent);
		
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.DOCUMENT);
		logActivity.setActiviy(DocumentActivityLogs.DELETE_DOC);
		logActivity.setDomainRef(file);
		logActivity.setDomainVersion(new DomainObjectVersion<IsoDocument>(IsoDocument.class, original, file));
		logActivity.setVersion(file.getLatestVersion());
		userActivityService.save(logActivity);
	}
	
	public List<IsoDocument> getFilesByCateogry(Category category) {
		return isoDocumentRepository.findByCategory(category.getId().toString());
	}
	
	public List<IsoDocument> getDeletedFileByCategory(Category category) {
		return isoDocumentRepository.findDeletedDocByCategory(category.getId().toString());
	}
	
	public GridFSDBFile getGridFSFile(User loginUser, String isoDocumentId) {
		IsoDocument document = isoDocumentRepository.findOne(isoDocumentId);
		return getGridFSFile(loginUser, document);
	}
	
	public GridFSDBFile getGridFSFile(User loginUser, IsoDocument isoDocument) {
		GridFSDBFile gridFile = isoDocumentRepository.retrieveGridFSFile(isoDocument.getFileId().toString());
		
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.DOCUMENT);
		logActivity.setActiviy(DocumentActivityLogs.DOWNLOAD_DOC);
		logActivity.setDomainRef(isoDocument);
		logActivity.setVersion(isoDocument.getLatestVersion());
		userActivityService.save(logActivity);
		
		return gridFile;
	}
	
	public long countFilesByCategory(Category category) {
		return isoDocumentRepository.countIsoDocumentsByCategoryId(category.getId().toString());
	}
	
	public IsoDocument getIsoDocumentAndSecurityById(String id, User accessUser) {
		IsoDocument document = isoDocumentRepository.findOne(id);
		if (CollectionUtils.isNotEmpty(document.getDocumentSecurities())) {
			document.setFinalSecurity(DomainSecurityChecker.checkDocumentSecurity(accessUser, document));
		}
		return document;
	}
	
	public List<IsoDocument> searchText(Organization organization, String searchText) {
		searchText = StringEscapeUtils.escapeSql(StringUtils.trimToEmpty(searchText));
		return isoDocumentRepository.searchText(organization.getId().toString(), searchText);
	}
	
	public List<IsoDocument> searchByCriteria(Organization organization, DocumentSearchCriteria criteria) {
		criteria.setOrganization(organization);
		return isoDocumentRepository.searchByCriteria(criteria);
	}
	
	public void restoreDocument(User loginUser, IsoDocument file) throws BusinessException {
		IsoDocument original = isoDocumentRepository.findOne(file.getId().toString());
		
		file.setDeletedBy(null);
		file.setDeletedOn(null);
		file.setDeleted(false);
		
		isoDocumentRepository.restoreIsoDocument(file);
		
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.DOCUMENT);
		logActivity.setActiviy(DocumentActivityLogs.RESTORE_DOCUMENT);
		logActivity.setDomainRef(file);
		logActivity.setDomainVersion(new DomainObjectVersion<IsoDocument>(IsoDocument.class, original, file));
		logActivity.setVersion(file.getLatestVersion());
		userActivityService.save(logActivity);
	}
	
	public void restoreCategoryAndDocument(User loginUser, IsoDocument file) throws BusinessException {
		
		Category category = categoryService.getCategoryById(file.getCategory().getId().toString());
		categoryService.restoreCategory(loginUser, category);
		
		IsoDocument original = isoDocumentRepository.findOne(file.getId().toString());
		
		file.setDeletedBy(null);
		file.setDeletedOn(null);
		file.setDeleted(false);
		
		isoDocumentRepository.save(file);
		
		UserActivity logActivity = new UserActivity(loginUser, LogTypes.DOCUMENT);
		logActivity.setActiviy(DocumentActivityLogs.RESTORE_DOCUMENT);
		logActivity.setDomainRef(file);
		logActivity.setDomainVersion(new DomainObjectVersion<IsoDocument>(IsoDocument.class, original, file));
		logActivity.setVersion(file.getLatestVersion());
		userActivityService.save(logActivity);
	}

	
}
