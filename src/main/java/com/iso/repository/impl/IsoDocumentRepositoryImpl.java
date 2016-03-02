package com.iso.repository.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.iso.constant.DocumentSearchCriteria;
import com.iso.domain.Category;
import com.iso.domain.IsoDocument;
import com.iso.domain.Organization;
import com.iso.repository.IsoDocumentRepositoryCustom;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

public class IsoDocumentRepositoryImpl implements IsoDocumentRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private GridFsTemplate gridFsTemplate;	

	@Override
	public GridFSFile insertGridFile(InputStream inputStream, String filename, String contentType, Organization organization) {
		
		DBObject metaData = new BasicDBObject();
		metaData.put(Organization.class.getSimpleName(), organization.getId().toString());
		
		GridFSFile gridfsFile = gridFsTemplate.store(inputStream, filename, contentType, metaData);		
		return gridfsFile;
	}

	@Override
	public GridFSFile insertGridTemplateFile(InputStream inputStream, String filename, String contentType, Organization organization) {
		
		DBObject metaData = new BasicDBObject();
		metaData.put(Organization.class.getSimpleName(), organization.getId().toString());
		metaData.put("template", true);
		
		GridFSFile gridfsFile = gridFsTemplate.store(inputStream, filename, contentType, metaData);		
		return gridfsFile;
	}
	
	public IsoDocument insertIsoDocument(IsoDocument file, GridFSFile gridfsFile) {
		file.setFileId((ObjectId)gridfsFile.getId());
		file.setExtension(FilenameUtils.getExtension(gridfsFile.getFilename()).toLowerCase());
		file.setFileTitle(FilenameUtils.getBaseName(gridfsFile.getFilename()));
		file.setContentType(gridfsFile.getContentType());
		file.setSize(gridfsFile.getLength());
		file.setCreatedOn(gridfsFile.getUploadDate());
		
		mongoTemplate.insert(file);
		return file;
	}
	
	@Override
	public IsoDocument updateIsoDocument(IsoDocument file) {
		mongoTemplate.save(file);
		return file;
	}

	@Override
	public void deleteIsoDocument(IsoDocument isoDocument, boolean permanent) {
		if (!permanent) {
			GridFSDBFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(isoDocument.getFileId())));
			if (!file.getMetaData().containsField("deleted")) {
				file.getMetaData().put("deleted", true);
				file.save();
			}
			
			isoDocument.setDeleted(true);
			mongoTemplate.save(isoDocument);
			
		} else {
			gridFsTemplate.delete(new Query(Criteria.where("_id").is(isoDocument.getFileId())));
			mongoTemplate.findAndRemove(new Query(Criteria.where("_id").is(isoDocument.getId())), IsoDocument.class);			
		}
	}

	@Override
	public GridFSDBFile retrieveGridFSFile(String fileId) {
		GridFSDBFile gridFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(fileId))));
		return gridFile;
	}

	@Override
	public long countIsoDocumentsByCategoryId(String id) {
		Query query = new Query(Criteria.where("category.$id").is(new ObjectId(id)));
		query.addCriteria(Criteria.where("deleted").ne(true));
		
		return mongoTemplate.count(query, IsoDocument.class);
	}

	@Override
	public List<IsoDocument> searchText(String organizationId, String searchStr) {
		
		Query query;
		if (StringUtils.isNotEmpty(searchStr)) {
			String[] words = searchStr.toLowerCase().split(" ");
			TextCriteria criteriaSearchText = TextCriteria.forDefaultLanguage().matchingAny(words);
			
			query = TextQuery.queryText(criteriaSearchText).sortByScore();
			query.addCriteria(Criteria.where("organization.$id").is(new ObjectId(organizationId)));
		} else {
			query = new Query(Criteria.where("organization.$id").is(new ObjectId(organizationId)));
		}
		
		query.addCriteria(Criteria.where("template").ne(true));
		query.addCriteria(Criteria.where("deleted").ne(true));
		query.addCriteria(Criteria.where("category").exists(true));
		List<IsoDocument> result = mongoTemplate.find(query, IsoDocument.class);
		
		return result;
	}

	@Override
	public List<IsoDocument> findByCategory(String categoryId) {
		Criteria findByMainCategory = Criteria.where("category.$id").is(new ObjectId(categoryId));
		Criteria findByAdditionalCategory = Criteria.where("additionalCategories").elemMatch(Criteria.where("$id").is(new ObjectId(categoryId)));
		
		Criteria criteria = new Criteria();
		criteria.orOperator(findByMainCategory, findByAdditionalCategory);
		
		Query query = Query.query(criteria);
		query.addCriteria(Criteria.where("deleted").ne(true));
		query.addCriteria(Criteria.where("template").ne(true));
		
		query.with(new Sort(Sort.Direction.ASC, "fileTitle"));
		
		List<IsoDocument> result = mongoTemplate.find(query, IsoDocument.class);
		return result;
	}

	@Override
	public List<IsoDocument> searchByCriteria(DocumentSearchCriteria criteria) {
		
		Query query;
		if (StringUtils.isNotEmpty(criteria.getSearchText())) {
			String[] words = criteria.getSearchText().trim().toLowerCase().split(" ");
			TextCriteria criteriaSearchText = TextCriteria.forDefaultLanguage().matchingAny(words);
			
			query = TextQuery.queryText(criteriaSearchText).sortByScore();
			query.addCriteria(Criteria.where("organization.$id").is(criteria.getOrganization().getId()));
		} else {
			query = new Query(Criteria.where("organization.$id").is(criteria.getOrganization().getId()));
		}
		query.addCriteria(Criteria.where("template").ne(true));
		
		if (criteria.getType() != null) {
			if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.NEW)) {
				query.addCriteria(Criteria.where("deleted").ne(true));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.CHANGED)) {
				query.addCriteria(Criteria.where("deleted").ne(true));
				if (criteria.getFrom() == null && criteria.getTo() == null) {
					query.addCriteria(Criteria.where("updatedOn").exists(true));
				}
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.DELETE)) {
				query.addCriteria(Criteria.where("deleted").is(true));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.LOCKED)) {
				query.addCriteria(Criteria.where("deleted").ne(true));
				query.addCriteria(Criteria.where("locked").is(true));
			}
		}

		if (criteria.getFrom() != null) {
			if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.NEW)) {
				query.addCriteria(Criteria.where("createdOn").gte(criteria.getFrom()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.CHANGED)) {
				query.addCriteria(Criteria.where("updatedOn").gte(criteria.getFrom()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.DELETE)) {
				query.addCriteria(Criteria.where("deletedOn").gte(criteria.getFrom()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.LOCKED)) {
				query.addCriteria(Criteria.where("lockedOn").gte(criteria.getFrom()));
			}
			
		}
		
		if (criteria.getTo() != null) {
			if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.NEW)) {
				query.addCriteria(Criteria.where("createdOn").lte(criteria.getTo()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.CHANGED)) {
				query.addCriteria(Criteria.where("updatedOn").lte(criteria.getTo()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.DELETE)) {
				query.addCriteria(Criteria.where("deletedOn").lte(criteria.getTo()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.LOCKED)) {
				query.addCriteria(Criteria.where("lockedOn").lte(criteria.getTo()));
				
			}
		}
		
		if (criteria.getUser() != null) {
			if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.NEW)) {
				query.addCriteria(Criteria.where("createdBy").is(criteria.getUser()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.CHANGED)) {
				query.addCriteria(Criteria.where("updatedBy").is(criteria.getUser()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.DELETE)) {
				query.addCriteria(Criteria.where("deletedBy").is(criteria.getUser()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.LOCKED)) {
				query.addCriteria(Criteria.where("lockedBy").is(criteria.getUser()));
				
			}
		}
		
		if (criteria.getSizeFrom() > 0) {
			query.addCriteria(Criteria.where("size").gte(criteria.getSizeFrom()*1024));
		}
		
		if (criteria.getSizeTo() > 0) {
			query.addCriteria(Criteria.where("size").lte(criteria.getSizeTo()*1024));
		}

		if (criteria.getCategory() != null && criteria.getCategory().getId() != null) {
			query.addCriteria(Criteria.where("category.$id").is(criteria.getCategory().getId()));
		} else {
			query.addCriteria(Criteria.where("category").exists(true));
		}
		
		if (criteria.getLimit() > 0) {
			query.limit(criteria.getLimit());
		}
		return mongoTemplate.find(query, IsoDocument.class);
	}

	@Override
	public List<IsoDocument> findDeletedDocByCategory(String categoryId) {
		Criteria findByMainCategory = Criteria.where("category.$id").is(new ObjectId(categoryId));
		Criteria findByAdditionalCategory = Criteria.where("additionalCategories").elemMatch(Criteria.where("$id").is(new ObjectId(categoryId)));
		
		Criteria criteria = new Criteria();
		criteria.orOperator(findByMainCategory, findByAdditionalCategory);
		
		Query query = Query.query(criteria);
		query.addCriteria(Criteria.where("deleted").is(true));
		query.addCriteria(Criteria.where("template").ne(true));
		
		query.with(new Sort(Sort.Direction.ASC, "fileTitle"));
		
		List<IsoDocument> result = mongoTemplate.find(query, IsoDocument.class);
		return result;
	}

	@Override
	public GridFSFile cloneGridFile(IsoDocument document) {
		GridFSDBFile originalGridFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(document.getFileId())));
		GridFSFile clonedGridfsFile = gridFsTemplate.store(originalGridFile.getInputStream(), originalGridFile.getFilename(), originalGridFile.getContentType(), originalGridFile.getMetaData());		
		return clonedGridfsFile;
	}

	@Override
	public List<IsoDocument> getListIsoDocumentByListFileID(String organizationId, List<String> fileIds) {
		
		List<ObjectId> objectIds = new ArrayList<ObjectId>();
		
		if (CollectionUtils.isNotEmpty(fileIds)) {
			for (String id : fileIds) {
				objectIds.add(new ObjectId(id));
			}
		}
		Criteria findByListFileId = Criteria.where("fileId").in(objectIds);
		
		Query query = Query.query(findByListFileId);
		query.addCriteria(Criteria.where("deleted").ne(true));
		query.addCriteria(Criteria.where("template").ne(true));
		query.addCriteria(Criteria.where("organization.$id").is(new ObjectId(organizationId)));
		
		query.with(new Sort(Sort.Direction.ASC, "fileTitle"));
		
		List<IsoDocument> result = mongoTemplate.find(query, IsoDocument.class);
		return result;
	}

	@Override
	public List<IsoDocument> getListIsoDocumentByListID(String organizationId, List<String> isoDocumentIds) {
		
		List<ObjectId> objectIds = new ArrayList<ObjectId>();
		
		if (CollectionUtils.isNotEmpty(isoDocumentIds)) {
			for (String id : isoDocumentIds) {
				objectIds.add(new ObjectId(id));
			}
		}
		
		Criteria findByListId = Criteria.where("_id").in(objectIds);
		
		Query query = Query.query(findByListId);
		query.addCriteria(Criteria.where("deleted").ne(true));
		query.addCriteria(Criteria.where("template").ne(true));
		query.addCriteria(Criteria.where("organization.$id").is(new ObjectId(organizationId)));
		
		query.with(new Sort(Sort.Direction.ASC, "fileTitle"));
		
		List<IsoDocument> result = mongoTemplate.find(query, IsoDocument.class);
		return result;
	}

	@Override
	public void restoreIsoDocument(IsoDocument isoDocument) {
		
		GridFSDBFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(isoDocument.getFileId())));
		if (!file.getMetaData().containsField("deleted")) {
			file.getMetaData().put("deleted", false);
			file.save();
		}
		
		isoDocument.setDeleted(true);
		mongoTemplate.save(isoDocument);
		
	}

	@Override
	public List<IsoDocument> findByDocumentSecurityOrganizationUnit(String organizationUnitId, String organizationId) {
		Query query = Query.query(Criteria.where("organization.$id").is(new ObjectId(organizationId)));
		query.addCriteria(Criteria.where("deleted").ne(true));
		query.addCriteria(Criteria.where("documentSecurities.orgUnit.$id").is(new ObjectId(organizationUnitId)));
		
		return mongoTemplate.find(query, IsoDocument.class);
	}

	@Override
	public List<IsoDocument> findByDocumentSecurityUser(String userId, String organizationId) {
		Query query = Query.query(Criteria.where("organization.$id").is(new ObjectId(organizationId)));
		query.addCriteria(Criteria.where("deleted").ne(true));
		query.addCriteria(Criteria.where("categorySecurities.user.$id").is(new ObjectId(userId)));
		
		return mongoTemplate.find(query, IsoDocument.class);
	}

}
