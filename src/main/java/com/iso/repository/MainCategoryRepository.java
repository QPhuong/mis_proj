package com.iso.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.iso.domain.MainCategory;
import com.iso.domain.MainCategoryType;

public interface MainCategoryRepository extends PagingAndSortingRepository<MainCategory, String>{

	public List<MainCategory> findByParentOrderByNameAsc(MainCategory parent);
	public List<MainCategory> findByParentOrderByNameDesc(MainCategory parent);
	
	public List<MainCategory> findByParentOrderByCreatedOnAsc(MainCategory parent);
	public List<MainCategory> findByParentOrderByCreatedOnDesc(MainCategory parent);
	
	public List<MainCategory> findByParentOrderByUpdatedOnAsc(MainCategory parent);
	public List<MainCategory> findByParentOrderByUpdatedOnDesc(MainCategory parent);
	
	public MainCategory findByRootAndType(boolean isRoot, MainCategoryType type);
}
