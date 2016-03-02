package com.iso.repository.impl;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.iso.domain.OrganizationUnit;
import com.iso.repository.OrganizationUnitRepositoryCustom;

public class OrganizationUnitRepositoryImpl implements OrganizationUnitRepositoryCustom{

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<OrganizationUnit> findByAssignedUser(String userId) {
		
		Query query = new Query(Criteria.where("users.$id").is(new ObjectId(userId)));
		
		return mongoTemplate.find(query, OrganizationUnit.class);
	}

}
