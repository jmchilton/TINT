package edu.umn.msi.tropix.storage.core.access.gridfs;

import java.io.InputStream;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public interface GridFsI {
  
  GridFSInputFile createFile(String filename);
  
  GridFSInputFile createFile(String filename, final InputStream inputStream);
  
  GridFSDBFile findOne(String filename);
  
  void remove(final String filename);
  
}
