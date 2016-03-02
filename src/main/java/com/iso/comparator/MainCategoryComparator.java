package com.iso.comparator;


import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.iso.constant.CategorySortValue;
import com.iso.constant.SortType;
import com.iso.domain.MainCategory;

public abstract class MainCategoryComparator implements Comparator<MainCategory>{

	private CategorySortValue sortValue;
	private SortType sortType;
	
	public MainCategoryComparator (CategorySortValue sortValue, SortType sortType){
		
	}
	@Override
	public int compare(MainCategory cat1, MainCategory cat2) {
		int res = 0;
		if(this.sortValue.compareTo(CategorySortValue.NAME)  == 0) {
			String str1 = StringUtils.trimToEmpty(cat1.getName());
			String str2 = StringUtils.trimToEmpty(cat2.getName());
			res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
	        if (res == 0) {
	            res = str1.compareTo(str2);
	        }	        
		} else if(this.sortValue.compareTo(CategorySortValue.DATE_CREATED)  == 0){
			Date date1 = cat1.getCreatedOn();
			Date date2 = cat2.getCreatedOn();
			res = date1.compareTo(date2);
		} else if(this.sortValue.compareTo(CategorySortValue.DATE_UPDATED)  == 0){
			Date date1 = cat1.getUpdatedOn();
			Date date2 = cat2.getUpdatedOn();
			res = date1.compareTo(date2);
		}
		
		if (sortType.compareTo(SortType.DESC) == 0) {
			res = res*(-1);
		}
		
		return res;
	}

}
