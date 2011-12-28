package edu.umn.msi.tropix.storage.service.impl;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.storage.core.FileMapper;
import edu.umn.msi.tropix.storage.core.StorageManager;
import edu.umn.msi.tropix.storage.core.StorageManager.UploadCallback;
import edu.umn.msi.tropix.storage.service.StorageService;

@ManagedBean
public class StorageServiceImpl implements StorageService {
  private StorageManager storageManager;
  private FileMapper fileMapper;

  @Inject
  public StorageServiceImpl(final StorageManager storageManager, @Named("httpStorageFileMapper") final FileMapper fileMapper) {
    this.storageManager = storageManager;
    this.fileMapper = fileMapper;
  }
  
  public String getData(final String userIdentity, final String dataId) {
    final InputContext downloadFile = storageManager.download(dataId, userIdentity);
    final String url = fileMapper.prepareDownload(downloadFile);
    return url;
  }

  public String putData(final String userIdentity, final String dataId) {
    final UploadCallback uploadCallback = storageManager.upload(dataId, userIdentity);
    final String url = fileMapper.prepareUpload(uploadCallback);
    return url;
  }

}
