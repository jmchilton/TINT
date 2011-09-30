package edu.umn.msi.tropix.grid.metadata.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.io.FileChangeMonitor;
import edu.umn.msi.tropix.common.io.OutputContexts;

public class FileMetadataUpdater<T> extends FileMetadataSetter<T> {
  private URL defaultMetadataLocation;
  private FileChangeMonitor fileChangeMonitor;

  public FileMetadataUpdater(final Class<T> metadataClass, final File metadataFile, final MetadataBean<T> metadataBean) {
    super(metadataClass, metadataFile, metadataBean);
  }
  
  @PostConstruct
  public void init() throws IOException {
    final File metadataFile = getMetadataFile();
    fileChangeMonitor.registerChangeListener(metadataFile, new Closure<File>() {
      public void apply(final File input) {
        update();
      }
    });
    if(defaultMetadataLocation != null && !metadataFile.exists()) {
      OutputContexts.forFile(metadataFile).put(defaultMetadataLocation);
    }
  }

  public void setDefaultMetadataLocation(final URL defaultMetadataLocation) {
    this.defaultMetadataLocation = defaultMetadataLocation;
  }

  public void setFileChangeMonitor(final FileChangeMonitor fileChangeMonitor) {
    this.fileChangeMonitor = fileChangeMonitor;
  }
}
