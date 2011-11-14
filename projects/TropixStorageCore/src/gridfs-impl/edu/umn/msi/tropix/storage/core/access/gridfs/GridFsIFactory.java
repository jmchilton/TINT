package edu.umn.msi.tropix.storage.core.access.gridfs;

import java.io.InputStream;

import com.google.common.base.Function;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

class GridFsIFactory implements Function<DB, GridFsI> {

  public GridFsI apply(DB db) {
    return apply(new GridFS(db));
  }
  
  public GridFsI apply(final GridFS gridFs) {
    return new GridFsImpl(gridFs);    
  }
  
  private static class GridFsImpl implements GridFsI {
    private GridFS gridFs;
    
    private GridFsImpl(final GridFS gridFs) {
      this.gridFs = gridFs;
    }
    
    public GridFSInputFile createFile(String filename) {
      return gridFs.createFile(filename);
    }

    public GridFSInputFile createFile(String filename, final InputStream inputStream) {
      return gridFs.createFile(inputStream, filename);
    }

    public GridFSDBFile findOne(String filename) {
      return gridFs.findOne(filename);
    }
    
    public void remove(final String filename) {
      gridFs.remove(filename);
    }
    
  }
  
}