package edu.umn.msi.tropix.client.directory.impl;

import info.minnesotapartnership.tropix.directory.TropixDirectoryService;

import java.util.Map;

public class MapTropixDirectoryServicePersonSupplierImpl extends BaseTropixDirectoryServicePersonSupplierImpl {
  private Map<String, TropixDirectoryService> serviceMap;

  public void setServiceMap(final Map<String, TropixDirectoryService> serviceMap) {
    this.serviceMap = serviceMap;
  }

  protected TropixDirectoryService getService(String address) {
    return serviceMap.get(address);
  }

}
