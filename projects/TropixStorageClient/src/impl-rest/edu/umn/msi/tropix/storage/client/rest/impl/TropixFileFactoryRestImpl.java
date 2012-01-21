package edu.umn.msi.tropix.storage.client.rest.impl;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.io.transfer.TransferResourceContextFactory;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.storage.client.impl.BaseStorageDataImpl;
import edu.umn.msi.tropix.storage.client.impl.TropixFileFactory;
import edu.umn.msi.tropix.storage.service.StorageService;
import edu.umn.msi.tropix.storage.service.client.StorageServiceFactory;
import edu.umn.msi.tropix.transfer.types.HttpTransferResource;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@ManagedBean
public class TropixFileFactoryRestImpl implements TropixFileFactory {
  private final StorageServiceFactory storageServiceFactory;
  private final TransferResourceContextFactory contextFactory;
  
  @Inject
  public TropixFileFactoryRestImpl(final StorageServiceFactory storageServiceFactory,
                                   final TransferResourceContextFactory contextFactory) {
    this.storageServiceFactory = storageServiceFactory;
    this.contextFactory = contextFactory;
  }

  public ModelStorageData getStorageData(final TropixFile tropixFile, final Credential credential) {
    return new StorageDataGridImpl(tropixFile, credential);
  }

  class StorageDataGridImpl extends BaseStorageDataImpl {

    StorageDataGridImpl(final TropixFile tropixFile, final Credential credential) {
      super(tropixFile, credential);
    }
    
    private StorageService getStorageService() {
      return storageServiceFactory.get(getTropixFile().getStorageServiceUrl());
    }

    public TransferResource prepareUploadResource() {      
      final String url = getStorageService().putData(getIdentity(), getDataIdentifier());
      return new HttpTransferResource(url);
    }

    public TransferResource prepareDownloadResource() {
      final String url = getStorageService().getData(getIdentity(), getDataIdentifier());
      return new HttpTransferResource(url);
    }

    public InputContext getDownloadContext() {
      return contextFactory.getDownloadContext(prepareDownloadResource(), getCredential());
    }

    public OutputContext getUploadContext() {
      return contextFactory.getUploadContext(prepareUploadResource(), getCredential());
    }

  }
}
