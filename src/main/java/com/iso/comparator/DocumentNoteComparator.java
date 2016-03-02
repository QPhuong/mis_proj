package com.iso.comparator;


import java.util.Comparator;
import java.util.Date;

import com.iso.domain.DocumentNote;

public class DocumentNoteComparator implements Comparator<DocumentNote>{

	public DocumentNoteComparator() {
	}
	
	@Override
	public int compare(DocumentNote domain1, DocumentNote domain2) {
		int res = 0;
		Date date1 = domain1.getCreatedOn();
		Date date2 = domain2.getCreatedOn();
		res = date2.compareTo(date1);
		return res;
	}

}
