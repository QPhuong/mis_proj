package com.iso.repository;

import java.util.List;

import com.iso.domain.DocumentVersion;
import com.iso.domain.IsoDocument;

public interface DocumentVersionRepositoryCustom {

	public List<DocumentVersion> findByIsoDocumentOrderByVersionDateDesc(IsoDocument file);
}
