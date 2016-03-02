package com.iso.repository.impl;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.iso.domain.DocumentNote;
import com.iso.repository.DocumentNoteRepositoryCustom;

public class DocumentNoteRepositoryImpl implements DocumentNoteRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<DocumentNote> findByIsoDocumentOrderByCreatedDateDesc(String documentId) {
		
		Criteria criteria = Criteria.where("isoDocument.$id").is(new ObjectId(documentId));
		Query query = new Query(criteria);
		query.with(new Sort(Sort.Direction.DESC, "createdOn"));
		
		return mongoTemplate.find(query, DocumentNote.class);
	}

}
