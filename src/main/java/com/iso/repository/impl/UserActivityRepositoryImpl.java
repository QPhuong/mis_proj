package com.iso.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.iso.constant.UserActivityCriteria;
import com.iso.domain.UserActivity;
import com.iso.repository.UserActivityRepositoryCustom;

public class UserActivityRepositoryImpl implements UserActivityRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<UserActivity> searchByCriteria(UserActivityCriteria criteria) {
	
		Query query = new Query();
		
		if (criteria.getLogType() != null) {
			Criteria findByLogType = Criteria.where("logType").is(criteria.getLogType().name());
			query.addCriteria(findByLogType);
		}
		
		if (criteria.getFrom() != null) {
			Criteria findByDateFrom = Criteria.where("activityDate").gte(criteria.getFrom());
			query.addCriteria(findByDateFrom);
		}
		
		if (criteria.getTo() != null) {
			Criteria findByDateTo = Criteria.where("activityDate").lte(criteria.getTo());
			query.addCriteria(findByDateTo);
		}
		
		if (criteria.getUser() != null && criteria.getUser().getId() != null) {
			Criteria findByUser = Criteria.where("user._id").is(criteria.getUser().getId());
			query.addCriteria(findByUser);
		}
		
		query.with(new Sort(Sort.Direction.DESC, "activityDate"));
		if (criteria.getLimit() > 0) {
			query.limit(criteria.getLimit());
		}
		
		return mongoTemplate.find(query, UserActivity.class);
	}


}
