package com.iso.repository.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.iso.domain.MainCategory;
import com.iso.domain.MainCategoryType;
import com.iso.repository.MainCategoryTypeRepositoryCustom;

public class MainCategoryTypeRepositoryImpl implements MainCategoryTypeRepositoryCustom{

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<MainCategoryType> findValidMainCategoryTypes() {
		Query query = new Query(Criteria.where("root").is(true));
		List<MainCategory> categoryLst = mongoTemplate.find(query, MainCategory.class);
		
		List<MainCategoryType> typeLst = new ArrayList<MainCategoryType>();
		if (CollectionUtils.isNotEmpty(categoryLst)) {
			for(MainCategory category : categoryLst) {
				if (category.getType() != null) {
					typeLst.add(category.getType());
				}
			}
		}
		return typeLst;
	}

	
}
