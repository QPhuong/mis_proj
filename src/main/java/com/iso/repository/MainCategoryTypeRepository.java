package com.iso.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.iso.domain.MainCategoryType;

public interface MainCategoryTypeRepository extends PagingAndSortingRepository<MainCategoryType, String>, MainCategoryTypeRepositoryCustom{

	public MainCategoryType findByCode(String code);
	
}
