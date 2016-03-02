package com.iso.repository.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.iso.domain.ApplicationConfiguration;
import com.iso.repository.ApplicationConfigurationRepositoryCustom;

public class ApplicationConfigurationRepositoryImpl implements
		ApplicationConfigurationRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTempalte;
	
	@Override
	public ApplicationConfiguration getLatestConfiguration() {
		Query query = new Query();
		query.limit(1);
		query.with(new Sort(Sort.Direction.DESC, "createdOn"));
		List<ApplicationConfiguration> lstConfig = mongoTempalte.find(query, ApplicationConfiguration.class);
		if (CollectionUtils.isNotEmpty(lstConfig)) {
			return lstConfig.get(0);
		}
		return null;
	}

}
