package com.iso.repository.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.iso.domain.Organization;
import com.iso.domain.User;
import com.iso.repository.UserRepositoryCustom;

public class UserRepositoryImpl implements UserRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<User> searchLikeName(Organization organization, String name) {
		
		Criteria criteria = Criteria.where("organization.$id").is(organization.getId());
		
		Criteria criteriaName = new Criteria();
		Criteria criteriaUserName = Criteria.where("username").regex(StringUtils.trim(name));
		Criteria criteriaFullName = Criteria.where("fullname").regex(StringUtils.trim(name));
		criteriaName.orOperator(criteriaUserName, criteriaFullName);
		
		criteria.andOperator(criteriaName);
		
		Query query = new Query(criteria);
		query = query.with(new Sort(Sort.Direction.ASC, "fullname"));
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public List<User> findUserAssignedToUserRole(String organizationId, String userRoleId) {
		
		Query query = new Query(Criteria.where("userRoles.$id").is(new ObjectId(userRoleId)));
		query.addCriteria(Criteria.where("organization.$id").is(new ObjectId(organizationId)));
		
		return mongoTemplate.find(query, User.class);
	}

	@Override
	public List<User> findUserAssignedToOrgUnit(String organizationId, String orgUnitId) {
		Query query = new Query(Criteria.where("organizationUnits.$id").is(new ObjectId(orgUnitId)));
		query.addCriteria(Criteria.where("organization.$id").is(new ObjectId(organizationId)));
		
		return mongoTemplate.find(query, User.class);
	}

}
