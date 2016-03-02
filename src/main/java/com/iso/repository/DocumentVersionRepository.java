package com.iso.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.iso.domain.DocumentVersion;

public interface DocumentVersionRepository extends PagingAndSortingRepository<DocumentVersion, String>, DocumentVersionRepositoryCustom{

	

}
