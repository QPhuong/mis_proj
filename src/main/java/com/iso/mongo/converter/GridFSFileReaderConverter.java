package com.iso.mongo.converter;


import org.springframework.core.convert.converter.Converter;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSFile;

public class GridFSFileReaderConverter implements Converter<DBObject, GridFSFile>{

	@Override
	public GridFSFile convert(DBObject source) {
		
		
		return null;
	}

}
