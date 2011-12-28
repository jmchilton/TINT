package edu.umn.msi.tropix.storage.client.rest.impl;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.storage.client.impl.BaseStorageDataImpl;
import edu.umn.msi.tropix.storage.client.impl.TropixFileFactory;
import edu.umn.msi.tropix.storage.service.client.StorageServiceFactory;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@ManagedBean
public class TropixFileFactoryRestImpl implements TropixFileFactory {
  private final StorageServiceFactory storageServiceFactory;
  
  @Inject
  public TropixFileFactoryRestImpl(final StorageServiceFactory storageServiceFactory) {
    this.storageServiceFactory = storageServiceFactory;
  }
  
  public ModelStorageData getStorageData(final TropixFile tropixFile, final Credential credential) {
    return new StorageDataGridImpl(tropixFile, credential);
  }

  
  class StorageDataGridImpl extends BaseStorageDataImpl {

    StorageDataGridImpl(final TropixFile tropixFile, final Credential credential) {
      super(tropixFile, credential);
    }

    @Override
    public OutputContext getUploadContext() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public TransferResource prepareUploadResource() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public TransferResource prepareDownloadResource() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public InputContext getDownloadContext() {
      // TODO Auto-generated method stub
      return null;
    }
    
  }
}
