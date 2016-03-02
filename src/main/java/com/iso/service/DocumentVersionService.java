package com.iso.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iso.domain.DocumentVersion;
import com.iso.domain.IsoDocument;

@Service
public interface DocumentVersionService extends Serializable{

	public List<DocumentVersion> getDocumentVersion(IsoDocument isoDocument);
}
