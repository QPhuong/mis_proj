package com.iso.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.iso.domain.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, String>, CategoryRepositoryCustom {

	public Category findByNameOrderByIdAsc(String name); 
	
}
