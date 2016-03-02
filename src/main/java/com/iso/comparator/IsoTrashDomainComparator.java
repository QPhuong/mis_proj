package com.iso.comparator;


import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.iso.constant.SortType;
import com.iso.model.IsoTrashDomain;

public class IsoTrashDomainComparator implements Comparator<IsoTrashDomain>{

	public enum SortField {
		NAME("name"), DATE_DELETED("dateDeleted"), DATE_MODIFIED("dateModified"), ITEM_TYPE("itemType");
		
		private final String text;

		private SortField(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}
	}
	
	private SortField sortField;
	private SortType sortType;
	
	public IsoTrashDomainComparator() {
		this.sortField = SortField.DATE_DELETED;
		this.sortType = SortType.DESC;
	}
	
	public IsoTrashDomainComparator (SortField sortField, SortType sortType){
		this.sortField = sortField;
		this.sortType = sortType;
	}
	
	@Override
	public int compare(IsoTrashDomain domain1, IsoTrashDomain domain2) {
		int res = 0;
		if(this.sortField.compareTo(SortField.NAME)  == 0) {
			String str1 = StringUtils.trimToEmpty(domain1.getName());
			String str2 = StringUtils.trimToEmpty(domain2.getName());
			res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
	        if (res == 0) {
	            res = str1.compareTo(str2);
	        }	        
		} else if(this.sortField.compareTo(SortField.DATE_DELETED)  == 0){
			Date date1 = domain1.getDateDeleted();
			Date date2 = domain2.getDateDeleted();
			if (date1 == null) {
				return 1;
			}else if (date2 == null) {
				return -1;
			}
			res = date1.compareTo(date2);
		} else if(this.sortField.compareTo(SortField.DATE_MODIFIED)  == 0){
			Date date1 = domain1.getDateModified();
			Date date2 = domain2.getDateModified();
			res = date1.compareTo(date2);
		} else if(this.sortField.compareTo(SortField.ITEM_TYPE)  == 0) {
			String str1 = StringUtils.trimToEmpty(domain1.getItemType());
			String str2 = StringUtils.trimToEmpty(domain2.getItemType());
			res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
	        if (res == 0) {
	            res = str1.compareTo(str2);
	        }	        
		} 
		
		if (sortType.compareTo(SortType.DESC) == 0) {
			res = res*(-1);
		}
		
		return res;
	}

}
