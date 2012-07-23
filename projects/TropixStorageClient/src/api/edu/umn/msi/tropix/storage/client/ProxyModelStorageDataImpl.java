package edu.umn.msi.tropix.storage.client;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class ProxyModelStorageDataImpl implements ModelStorageData {
  private final ModelStorageData proxy;

  protected ModelStorageData getProxy() {
    return proxy;
  }

  public ProxyModelStorageDataImpl(final ModelStorageData proxy) {
    this.proxy = proxy;
  }

  public OutputContext getUploadContext() {
    return proxy.getUploadContext();
  }

  public TransferResource prepareUploadResource() {
    return proxy.prepareUploadResource();
  }

  public TransferResource prepareDownloadResource() {
    return proxy.prepareDownloadResource();
  }

  public InputContext getDownloadContext() {
    return proxy.getDownloadContext();
  }

  public String getDataIdentifier() {
    return proxy.getDataIdentifier();
  }

  public TropixFile getTropixFile() {
    return proxy.getTropixFile();
  }

}
