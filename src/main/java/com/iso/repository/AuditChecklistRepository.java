package com.iso.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.iso.domain.AssessmentControl;

public interface AuditChecklistRepository extends PagingAndSortingRepository<AssessmentControl, String>{

}
