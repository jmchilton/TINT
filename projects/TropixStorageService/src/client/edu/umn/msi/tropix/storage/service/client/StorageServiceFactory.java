package edu.umn.msi.tropix.storage.service.client;

import edu.umn.msi.tropix.storage.service.StorageService;

public interface StorageServiceFactory {
  
  StorageService get(final String address);

}
