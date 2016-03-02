package com.iso.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.iso.domain.UserSessionActivity;
import com.iso.repository.UserSessionActivityRepositoryCustom;

public class UserSessionActivityRepositoryImpl implements UserSessionActivityRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public UserSessionActivity findByUserIdAndIPAddressAndSessionId(String userId, String ipAddress, String sessionId) {
		Criteria criteria = Criteria.where("user.$id").is(userId);
		criteria = criteria.andOperator( Criteria.where("ipAddress").is(ipAddress).andOperator(Criteria.where("sessionId").is(sessionId)));
		Query query = new Query(criteria);
		
		return mongoTemplate.findOne(query, UserSessionActivity.class);
	}

	@Override
	public long countByUserId(String userId) {
		Criteria criteria = Criteria.where("user.$id").is(userId);
		Query query = new Query(criteria);
		
		return mongoTemplate.count(query, UserSessionActivity.class);
	}

}
