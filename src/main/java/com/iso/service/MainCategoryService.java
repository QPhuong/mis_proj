package com.iso.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iso.constant.CategorySortValue;
import com.iso.constant.SortType;
import com.iso.domain.MainCategory;
import com.iso.domain.MainCategoryType;
import com.iso.domain.User;
import com.iso.exception.BusinessException;
import com.iso.model.IsoFile;

@Service
public interface MainCategoryService extends Serializable{

	public MainCategory getMainCategoryById(String id);
	
	public List<MainCategory> getSubMainCategoryByParent(MainCategory parent, CategorySortValue sortValue, SortType sortType);
	
	public MainCategory save(User loginUser, MainCategory category, List<IsoFile> uploadedFiles) throws IOException, BusinessException;
	
	public void delete(User loginUser, MainCategory category) throws BusinessException;
	
	public MainCategory getRootCategoryByCategoryType(MainCategoryType categoryType);
}
