package com.iso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iso.constant.DocumentSearchCriteria;
import com.iso.domain.IsoDocument;
import com.iso.domain.Organization;
import com.iso.elasticsearch.configuration.ElasticsearchConfiguration;
import com.iso.repository.IsoDocumentRepository;
import com.iso.service.DocumentElasticsearchService;

@Component(value="documentElasticsearchService")
public class DocumentElasticsearchServiceImpl implements DocumentElasticsearchService{

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private IsoDocumentRepository documentRepository;
/*
	public List<IsoDocument> simpleSearchDocument(DocumentSearchCriteria criteria) {
		List<IsoDocument> lstDoc = new ArrayList<IsoDocument>();
		
		Client client = getClientConfig();
		
		
		BoolQueryBuilder queryDocumentAllConditions = QueryBuilders.boolQuery()
								.mustNot(QueryBuilders.termQuery("deleted", "true"))
								.mustNot(QueryBuilders.termQuery("template", "true"))
								.must(QueryBuilders.termQuery("organization.id", criteria.getOrganization().getId().toString()));
		
		if (StringUtils.isNotEmpty(criteria.getSearchText())) {
			queryDocumentAllConditions.must(QueryBuilders.queryStringQuery(criteria.getSearchText()));
		}
		
		QueryBuilder queryDocument = QueryBuilders.filteredQuery(queryDocumentAllConditions, FilterBuilders.existsFilter("category"));
		
		SearchRequestBuilder searchDocumentInfo = client.prepareSearch("isodocument")
				.addFields("_id", "fileId", "fileTitle", "contentType")
				.setSearchType(SearchType.QUERY_THEN_FETCH)
				.setQuery(queryDocument);
		
		MultiSearchResponse multiSearchReponse;
		
		if (StringUtils.isNotEmpty(criteria.getSearchText())) {
			
			QueryBuilder queryFileContent = QueryBuilders.boolQuery()
					.must(QueryBuilders.queryStringQuery(criteria.getSearchText()))
					.must(QueryBuilders.termQuery("Organization", criteria.getOrganization().getId().toString()));
			
			SearchRequestBuilder searchFileContent = client.prepareSearch("isofile")
					.addFields("_id", "filename", "contentType", "length", "uploadDate")
					.setSearchType(SearchType.QUERY_THEN_FETCH)
					.setQuery(queryFileContent);
			
			multiSearchReponse = client.prepareMultiSearch()
					.add(searchDocumentInfo)
					.add(searchFileContent)
					.execute()
					.actionGet();
		}else {
			multiSearchReponse = client.prepareMultiSearch()
					.add(searchDocumentInfo)
					.execute()
					.actionGet();
		}
		
		List<String> fieldIDs = new ArrayList<String>();
		
		for (MultiSearchResponse.Item item : multiSearchReponse.getResponses()) {
			SearchResponse response = item.getResponse();
			for (SearchHit hit : response.getHits()) {
		        if (hit.getFields().containsKey("fileId")) {
		        	String fileId = hit.getFields().get("fileId").getValue().toString();
		        	if (!fieldIDs.contains(fileId)) {
			        	fieldIDs.add(fileId);
			        }	
		        }else {
					if (!fieldIDs.contains(hit.getId())) {
			        	fieldIDs.add(hit.getId());
			        }		
		        }
		    }
		}
		
		lstDoc = documentRepository.getListIsoDocumentByListFileID(criteria.getOrganization().getId().toString(), fieldIDs);
		
		return lstDoc;
	}*/
	
	public List<IsoDocument> searchDocument(DocumentSearchCriteria criteria, boolean simpleSearch) throws IOException {
		List<IsoDocument> lstDoc = new ArrayList<IsoDocument>();
		
		Client client = getClientConfig();
		
		//search document metadata
		BoolQueryBuilder queryDocumentBasicConditions = QueryBuilders.boolQuery()
								.mustNot(QueryBuilders.termQuery("deleted", "true"))
								.mustNot(QueryBuilders.termQuery("template", "true"))
								.must(QueryBuilders.termQuery("organization.id", criteria.getOrganization().getId().toString()));
		
		QueryBuilder queryDocument;
		
		if (StringUtils.isNotEmpty(criteria.getSearchText())) {
			List<String> fileContentSearchResult = searchFileContent(criteria);
			
			QueryBuilder queryMatchDocument = QueryBuilders.boolQuery()
					.must(queryDocumentBasicConditions)
					.must(QueryBuilders.queryStringQuery("*" + StringUtils.trim(criteria.getSearchText()) + "*"));
			QueryBuilder queryMatchFileContent = QueryBuilders.boolQuery()
					.must(queryDocumentBasicConditions)
					.must(QueryBuilders.termsQuery("fileId", fileContentSearchResult));
			
			queryDocument = QueryBuilders.boolQuery()
					.should(queryMatchDocument)
					.should(queryMatchFileContent);
		}else {
			queryDocument = queryDocumentBasicConditions;
		}
		
		if (!simpleSearch) {
			BoolFilterBuilder filterDocumentDetailCondition = createFilterDocumentAdvanceConditions(criteria);
			if (filterDocumentDetailCondition.hasClauses()) {
				queryDocument = QueryBuilders.filteredQuery(queryDocument, filterDocumentDetailCondition);
			}
		}
		
		SearchRequestBuilder searchDocumentInfo = client
				.prepareSearch(ElasticsearchConfiguration.getPropertiesValues(ElasticsearchConfiguration.ELASTICSEARCH_ISO_DOCUMENT_INDEX_NAME))
				.addFields("_id", "fileId", "fileTitle", "contentType")
				.setSearchType(SearchType.QUERY_THEN_FETCH)
				.setQuery(queryDocument)
				.addSort(SortBuilders.fieldSort("fileTitle").order(SortOrder.ASC));
		
		if (criteria.getLimit() > 0) {
			searchDocumentInfo.setFrom(0).setSize(criteria.getLimit());
		}
		
		List<String> documentIDs = new ArrayList<String>();
		
		SearchResponse response = searchDocumentInfo.execute().actionGet();
		
		for (SearchHit hit : response.getHits()) {
			if (!documentIDs.contains(hit.getId())) {
	        	documentIDs.add(hit.getId());
	        }		
	    }
		
		lstDoc = documentRepository.getListIsoDocumentByListID(criteria.getOrganization().getId().toString(), documentIDs);
		
		return lstDoc;
	}

	private Client getClientConfig() throws IOException {
		
		String host = ElasticsearchConfiguration.getPropertiesValues(ElasticsearchConfiguration.ELASTICSEARCH_HOST);
		int port = Integer.parseInt(ElasticsearchConfiguration.getPropertiesValues(ElasticsearchConfiguration.ELASTICSEARCH_PORT));
		
		@SuppressWarnings("resource")
		Client client = new TransportClient().addTransportAddress(new InetSocketTransportAddress(host, port));
		return client;
	}

	private BoolFilterBuilder createFilterDocumentAdvanceConditions(
			DocumentSearchCriteria criteria) {
		BoolFilterBuilder filterDocumentDetailCondition = FilterBuilders.boolFilter();
		if(criteria.getFrom() != null) {
			BoolFilterBuilder queryFrom = FilterBuilders.boolFilter()
					.should(FilterBuilders.rangeFilter("createdOn").gte(criteria.getFrom()))
					.should(FilterBuilders.rangeFilter("updatedOn").gte(criteria.getFrom()))
					.should(FilterBuilders.rangeFilter("lockedOn").gte(criteria.getFrom()));
			
			filterDocumentDetailCondition.must(queryFrom);
		}
		if(criteria.getTo() != null) {
			BoolFilterBuilder queryTo = FilterBuilders.boolFilter() 
					.should(FilterBuilders.rangeFilter("createdOn").lte(criteria.getTo()))
					.should(FilterBuilders.rangeFilter("updatedOn").lte(criteria.getTo()))
					.should(FilterBuilders.rangeFilter("lockedOn").lte(criteria.getTo()));
			filterDocumentDetailCondition.must(queryTo);
		}
		
		if (criteria.getUser() != null) {
			BoolFilterBuilder queryUser = FilterBuilders.boolFilter() 
					.should(FilterBuilders.termFilter("createdBy.id", criteria.getUser().getId().toString()))
					.should(FilterBuilders.termFilter("updatedBy.id", criteria.getUser().getId().toString()))
					.should(FilterBuilders.termFilter("lockedBy.id", criteria.getUser().getId().toString()));;
			filterDocumentDetailCondition.must(queryUser);
		}
		
		if (criteria.getSizeFrom() > 0) {
			RangeFilterBuilder querySizeFrom = FilterBuilders.rangeFilter("size").gte(criteria.getSizeFrom());
			filterDocumentDetailCondition.must(querySizeFrom);
		}
		if (criteria.getSizeTo() > 0) {
			RangeFilterBuilder querySizeTo = FilterBuilders.rangeFilter("size").lte(criteria.getSizeTo());
			filterDocumentDetailCondition.must(querySizeTo);
		}
		if (criteria.getCategory() != null && criteria.getCategory().getId() != null) {
			TermFilterBuilder queryCategory = FilterBuilders.termFilter("category.id", criteria.getCategory().getId().toString());
			filterDocumentDetailCondition.must(queryCategory);
		}
		return filterDocumentDetailCondition;
	}
	
	private List<String> searchFileContent(DocumentSearchCriteria criteria) throws IOException{
		//search file content
		Client client = getClientConfig();
		
		QueryBuilder queryFileContent = QueryBuilders.boolQuery()
				.must(QueryBuilders.queryStringQuery("*" + StringUtils.trim(criteria.getSearchText()) + "*"))
				.must(QueryBuilders.termQuery("metadata.Organization", criteria.getOrganization().getId().toString()));
		
		QueryBuilder query = QueryBuilders.filteredQuery(queryFileContent, FilterBuilders.boolFilter().mustNot(FilterBuilders.existsFilter("metadata.template")));
		
		SearchResponse response = client.prepareSearch(ElasticsearchConfiguration.getPropertiesValues(ElasticsearchConfiguration.ELASTICSEARCH_ISO_FILE_INDEX_NAME))
				.addFields("_id", "filename", "contentType", "length", "uploadDate")
				.setSearchType(SearchType.QUERY_THEN_FETCH)
				.setQuery(query)
				.execute()
				.actionGet();
		
		List<String> fileIDs = new ArrayList<String>();		
		for (SearchHit hit : response.getHits()) {
			if (!fileIDs.contains(hit.getId())) {
	        	fileIDs.add(hit.getId());
	        }		
	    }
		
		return fileIDs;
	}
	
	public void createDocumentIndex() throws IOException {
		Client client = getClientConfig();
		
		client.index(Requests.indexRequest("_river"));
		
		//client.index(Requests.indexRequest("_river").type("my_river").id("_meta").source(source)).actionGet();
		//my_river is the name of the index to create 
	}
	
	/*public static void main(String[] args) {
		try {
			DocumentElasticsearchService service = new DocumentElasticsearchServiceImpl();
			
			DocumentSearchCriteria criteria = new DocumentSearchCriteria();
			criteria.setSearchText("e");
			
			Organization org = new Organization();
			org.setId(new ObjectId("5561be6b6b2cdfc1419af72d"));
			
			criteria.setOrganization(org);
			
			service.searchDocument(criteria, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
