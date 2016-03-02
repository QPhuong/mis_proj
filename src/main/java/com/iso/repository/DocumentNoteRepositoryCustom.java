package com.iso.repository;

import java.util.List;

import com.iso.domain.DocumentNote;

public interface DocumentNoteRepositoryCustom {

	public List<DocumentNote> findByIsoDocumentOrderByCreatedDateDesc(String documentId);
}
