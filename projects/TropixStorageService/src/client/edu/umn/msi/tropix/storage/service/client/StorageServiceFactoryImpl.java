package edu.umn.msi.tropix.storage.service.client;

import javax.annotation.ManagedBean;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;

import edu.umn.msi.tropix.storage.service.StorageService;

@ManagedBean
public class StorageServiceFactoryImpl implements StorageServiceFactory {

  public StorageServiceFactoryImpl() {
    System.out.println("Constructing");
  }
  
  public StorageService get(final String address) {
    return JAXRSClientFactory.create(address, StorageService.class);
  }

}
