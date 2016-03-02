package com.iso.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.iso.repository.UserRoleRepositoryCustom;

public class UserRoleRepositoryImpl implements UserRoleRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

}
