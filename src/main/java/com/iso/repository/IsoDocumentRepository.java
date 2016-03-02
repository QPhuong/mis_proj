package com.iso.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.iso.domain.Category;
import com.iso.domain.IsoDocument;

public interface IsoDocumentRepository extends PagingAndSortingRepository<IsoDocument, String>, IsoDocumentRepositoryCustom {

	List<IsoDocument> findByCategoryOrderByIdAsc(Category category);
}
