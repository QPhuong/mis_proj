package com.iso.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.iso.domain.DocumentVersion;
import com.iso.domain.IsoDocument;
import com.iso.repository.DocumentVersionRepositoryCustom;

public class DocumentVersionRepositoryImpl implements DocumentVersionRepositoryCustom{
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<DocumentVersion> findByIsoDocumentOrderByVersionDateDesc(
			IsoDocument file) {
		Query query = new Query(Criteria.where("isoDocument._id").is(file.getId()));
		query.with(new Sort(Sort.Direction.DESC, "versionDate"));
		return mongoTemplate.find(query, DocumentVersion.class);	
	}

}
