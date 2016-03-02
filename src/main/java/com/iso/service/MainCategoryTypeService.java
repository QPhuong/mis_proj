package com.iso.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iso.domain.MainCategoryType;
import com.iso.domain.User;
import com.iso.exception.BusinessException;

@Service
public interface MainCategoryTypeService extends Serializable{

	public MainCategoryType save(User loginUser, MainCategoryType categoryType) throws BusinessException;
	
	public void delete(User loginUser, MainCategoryType categoryType) throws BusinessException;
	
	public List<MainCategoryType> getAllMainCategoryType();
	
	public List<MainCategoryType> getAllValidMainCategoryTypes() ;
	
	public MainCategoryType getById(String id);
	
	public MainCategoryType getByCode(String code);
	
}
