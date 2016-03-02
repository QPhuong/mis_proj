package com.iso.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.iso.domain.Organization;
import com.iso.repository.OrganizationRepositoryCustom;

public class OrganizationRepositoryImpl implements OrganizationRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;
		
	@Override
	public List<Organization> searchLikeName(String name) {
		Query query = new Query(Criteria.where("name").regex(name));
		List<Organization> organizationLst = mongoTemplate.find(query, Organization.class);
		return organizationLst;
	}
	
	

}
