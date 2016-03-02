package com.iso.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iso.constant.DocumentSearchCriteria;
import com.iso.domain.IsoDocument;

@Service
public interface DocumentElasticsearchService extends Serializable{

	public List<IsoDocument> searchDocument(DocumentSearchCriteria criteria, boolean simpleSearch) throws IOException;
}
