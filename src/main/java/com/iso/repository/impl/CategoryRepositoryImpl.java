package com.iso.repository.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.iso.constant.CategorySortValue;
import com.iso.constant.DocumentSearchCriteria;
import com.iso.constant.SortType;
import com.iso.domain.Category;
import com.iso.domain.Organization;
import com.iso.repository.CategoryRepositoryCustom;

public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private GridFsTemplate gridFsTemplate;

	@Override
	public List<Category> findByParentCategoryIdOrderByNameAsc(String parentId) {
		Query query = new Query(Criteria.where("parent.$id").is(new ObjectId(parentId))).with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(Criteria.where("deleted").ne(true));
		List<Category> categoryLst = mongoTemplate.find(query, Category.class);
		return categoryLst;
	}

	@Override
	public long countChildByParentId(String id) {
		Query query = new Query(Criteria.where("parent.$id").is(new ObjectId(id)));
		return mongoTemplate.count(query, Category.class);
	}

	@Override
	public List<Category> findByParentCategory(String parentId, SortType sortType, CategorySortValue sortValue) {
		Criteria criteria = Criteria.where("parent.$id").is(new ObjectId(parentId));
		Sort sort;
		if (sortType.equals(SortType.ASC)) {
			sort = new Sort(Sort.Direction.ASC, sortValue.getFieldName());
		}else {
			sort = new Sort(Sort.Direction.DESC, sortValue.getFieldName());
		}
		Query query = new Query(criteria);
		query.addCriteria(Criteria.where("deleted").ne(true));
		query.with(sort);
		List<Category> categoryLst = mongoTemplate.find(query, Category.class);
		return categoryLst;
	}

	@Override
	public Category findRootCategoryByOrganization(String organizationId) {
		Query query = Query.query(Criteria.where("organization.$id").is(new ObjectId(organizationId)));
		query.addCriteria(Criteria.where("root").is(true));
		return mongoTemplate.findOne(query, Category.class);
	}
	
	@Override
	public List<Category> searchByCriteria(Organization organization, DocumentSearchCriteria criteria) {
		Query query;
		if (StringUtils.isNotEmpty(criteria.getSearchText())) {
			String[] words = criteria.getSearchText().trim().toLowerCase().split(" ");
			TextCriteria criteriaSearchText = TextCriteria.forDefaultLanguage().matchingAny(words);
			
			query = TextQuery.queryText(criteriaSearchText).sortByScore();
			query.addCriteria(Criteria.where("organization.$id").is(organization.getId()));
		} else {
			query = new Query(Criteria.where("organization.$id").is(organization.getId()));
		}
		
		if (criteria.getType() != null) {
			if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.NEW)) {
				query.addCriteria(Criteria.where("deleted").ne(true));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.CHANGED)) {
				query.addCriteria(Criteria.where("deleted").ne(true));
				if (criteria.getFrom() == null && criteria.getTo() == null) {
					query.addCriteria(Criteria.where("updatedOn").exists(true));
				}
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.DELETE)) {
				query.addCriteria(Criteria.where("deleted").is(true));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.LOCKED)) {
				query.addCriteria(Criteria.where("deleted").ne(true));
				query.addCriteria(Criteria.where("locked").is(true));
			}
		}

		if (criteria.getFrom() != null) {
			if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.NEW)) {
				query.addCriteria(Criteria.where("createdOn").gte(criteria.getFrom()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.CHANGED)) {
				query.addCriteria(Criteria.where("updatedOn").gte(criteria.getFrom()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.DELETE)) {
				query.addCriteria(Criteria.where("deletedOn").gte(criteria.getFrom()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.LOCKED)) {
				query.addCriteria(Criteria.where("lockedOn").gte(criteria.getFrom()));
				
			}
			
		}
		
		if (criteria.getTo() != null) {
			if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.NEW)) {
				query.addCriteria(Criteria.where("createdOn").lte(criteria.getTo()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.CHANGED)) {
				query.addCriteria(Criteria.where("updatedOn").lte(criteria.getTo()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.DELETE)) {
				query.addCriteria(Criteria.where("deletedOn").lte(criteria.getTo()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.LOCKED)) {
				query.addCriteria(Criteria.where("lockedOn").lte(criteria.getTo()));
				
			}
		}
		
		if (criteria.getUser() != null) {
			if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.NEW)) {
				query.addCriteria(Criteria.where("createdBy").is(criteria.getUser()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.CHANGED)) {
				query.addCriteria(Criteria.where("updatedBy").is(criteria.getUser()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.DELETE)) {
				query.addCriteria(Criteria.where("deletedBy").is(criteria.getUser()));
				
			} else if (criteria.getType().equals(DocumentSearchCriteria.DocumentSearchType.LOCKED)) {
				query.addCriteria(Criteria.where("lockedBy").is(criteria.getUser()));
				
			}
		}

		if (criteria.getLimit() > 0) {
			query.limit(criteria.getLimit());
		}
		return mongoTemplate.find(query, Category.class);
	}

	@Override
	public List<Category> findDeletedCategoryByParent(String parentId) {
		Query query = new Query(Criteria.where("parent.$id").is(new ObjectId(parentId))).with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(Criteria.where("deleted").is(true));
		List<Category> categoryLst = mongoTemplate.find(query, Category.class);
		return categoryLst;
	}

	@Override
	public List<Category> searchByControlNumber(String controlNumber, String organizationId) {
		Criteria criteria = new Criteria();
		Criteria criteriaCode = Criteria.where("code").regex(StringUtils.trim(controlNumber) + "[^.]");
		Criteria criteriaName = Criteria.where("name").regex(StringUtils.trim(controlNumber) + "[^.]");
		criteria.orOperator(criteriaCode, criteriaName);
				
		Query query = Query.query(Criteria.where("deleted").ne(true));
		query.addCriteria(Criteria.where("organization.$id").is(new ObjectId(organizationId)));
		query.addCriteria(criteria);
		query.with(new Sort(Sort.Direction.ASC, "fileTitle"));
		
		List<Category> result = mongoTemplate.find(query, Category.class);
		return result;
	}

	@Override
	public List<Category> findByCategorySecurityOrganizationUnit(String organizationUnitId, String organizationId) {
		Query query = Query.query(Criteria.where("organization.$id").is(new ObjectId(organizationId)));
		query.addCriteria(Criteria.where("deleted").ne(true));
		query.addCriteria(Criteria.where("categorySecurities.orgUnit.$id").is(new ObjectId(organizationUnitId)));
		
		return mongoTemplate.find(query, Category.class);
	}

	@Override
	public List<Category> findByCategorySecurityUser(String userId, String organizationId) {
		Query query = Query.query(Criteria.where("organization.$id").is(new ObjectId(organizationId)));
		query.addCriteria(Criteria.where("deleted").ne(true));
		query.addCriteria(Criteria.where("categorySecurities.user.$id").is(new ObjectId(userId)));
		
		return mongoTemplate.find(query, Category.class);
	}

}
