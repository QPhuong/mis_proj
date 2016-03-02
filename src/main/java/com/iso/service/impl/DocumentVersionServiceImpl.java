package com.iso.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.iso.domain.DocumentVersion;
import com.iso.domain.IsoDocument;
import com.iso.repository.DocumentVersionRepository;
import com.iso.service.DocumentVersionService;

@Component(value="documentVersionService")
public class DocumentVersionServiceImpl implements DocumentVersionService{

	private static final long serialVersionUID = 1L;

	@Autowired
	private DocumentVersionRepository docVersionRepo;
	
	
	public List<DocumentVersion> getDocumentVersion(IsoDocument isoDocument) {
		return docVersionRepo.findByIsoDocumentOrderByVersionDateDesc(isoDocument);
	}
}
