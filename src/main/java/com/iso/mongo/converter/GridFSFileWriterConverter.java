package com.iso.mongo.converter;

import org.springframework.core.convert.converter.Converter;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSFile;

public class GridFSFileWriterConverter implements Converter<GridFSFile, DBObject>{

	@Override
	public DBObject convert(GridFSFile source) {
		// TODO Auto-generated method stub
		return null;
	}

}
