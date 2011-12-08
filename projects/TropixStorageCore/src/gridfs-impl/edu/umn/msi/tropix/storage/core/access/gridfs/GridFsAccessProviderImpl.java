package edu.umn.msi.tropix.storage.core.access.gridfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Supplier;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import edu.umn.msi.tropix.common.io.HasStreamInputContext;
import edu.umn.msi.tropix.common.io.IORuntimeException;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.storage.core.StorageManager.FileMetadata;
import edu.umn.msi.tropix.storage.core.access.AccessProvider;

@Named("gridfsAccessProvider")
public class GridFsAccessProviderImpl implements AccessProvider {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private Supplier<GridFsI> gridFsISupplier;
  
  @Inject
  public GridFsAccessProviderImpl(@Named("gridFsISupplier") final Supplier<GridFsI> gridFsISupplier) {
    this.gridFsISupplier = gridFsISupplier;
  }
  
  private GridFsI get() {
    return gridFsISupplier.get();
  }
  
  private static class GridFSDBFileInputContext implements HasStreamInputContext {
    private GridFSDBFile file;
    
    GridFSDBFileInputContext(final GridFSDBFile file) {
      this.file = file;
    }
    
    public InputStream asInputStream() {
      return file.getInputStream();
    }

    public void get(final OutputStream outputStream) {
      try {
        file.writeTo(outputStream);
      } catch(IOException e) {
        throw new IORuntimeException(e);
      }
    }

    public void get(File destination) {
      try {
        file.writeTo(destination);
      } catch(IOException e) {
        throw new IORuntimeException(e);
      }      
    }

    public void get(final OutputContext outputContext) {
      final InputStream inputStream = asInputStream();
      try {
        outputContext.put(inputStream);
      } finally {
        IO_UTILS.closeQuietly(inputStream);
      }
    }
    
  }
  
  
  public HasStreamInputContext getFile(final String id) {
    final GridFSDBFile file = getDbFile(id);
    return new GridFSDBFileInputContext(file);
  }

  public long putFile(final String id, final InputStream inputStream) {
    final GridFSInputFile inputFile = get().createFile(id, inputStream);
    inputFile.save();    
    return inputFile.getLength();
  }

  public OutputStream getPutFileOutputStream(final String id) {
    final GridFSInputFile inputFile = get().createFile(id);
    return inputFile.getOutputStream();
  }

  public boolean deleteFile(final String id) {
    get().remove(id);
    return true;
  }

  public boolean fileExists(final String id) {
    return getDbFile(id) != null;
  }

  public long getLength(final String id) {
    return getDbFile(id).getLength();
  }

  private GridFSDBFile getDbFile(final String id) {
    return get().findOne(id);
  }

  public long getDateModified(final String id) {
    return getDbFile(id).getUploadDate().getTime();
  }

  public FileMetadata getFileMetadata(String id) {
    final GridFSDBFile dbFile = getDbFile(id);
    final long dateModified = dbFile.getUploadDate().getTime();
    final long length = dbFile.getLength();
    return new FileMetadata(dateModified, length);
  }

}
