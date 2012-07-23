package edu.umn.msi.tropix.storage.client.impl;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.storage.client.ProxyModelStorageDataImpl;
import edu.umn.msi.tropix.storage.core.StorageManager;

public class CachingTropixFileFactoryImpl implements TropixFileFactory {
  private final TropixFileFactory baseFactory;
  private final StorageManager storageManager;

  public CachingTropixFileFactoryImpl(final TropixFileFactory baseFactory, final StorageManager storageManager) {
    this.baseFactory = baseFactory;
    this.storageManager = storageManager;
  }

  private class CachingModelStorageDataImpl extends ProxyModelStorageDataImpl implements ModelStorageData {
    final Credential credential;

    public CachingModelStorageDataImpl(final ModelStorageData proxy, final Credential credential) {
      super(proxy);
      this.credential = credential;
    }

    @Override
    public InputContext getDownloadContext() {
      final ModelStorageData proxy = super.getProxy();
      final String dataId = proxy.getDataIdentifier();
      final String callerId = credential.getIdentity();
      if(storageManager.canDownload(dataId, callerId)) {
        return storageManager.download(dataId, callerId);
      } else {
        return super.getDownloadContext();
      }
    }

  }

  public ModelStorageData getStorageData(TropixFile tropixFile, Credential credential) {
    ModelStorageData storageData = baseFactory.getStorageData(tropixFile, credential);
    if(storageManager != null) {
      storageData = new CachingModelStorageDataImpl(storageData, credential);
    }
    return storageData;
  }

}
