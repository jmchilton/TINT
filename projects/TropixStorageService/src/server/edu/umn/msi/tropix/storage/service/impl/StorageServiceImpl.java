package edu.umn.msi.tropix.storage.service.impl;

import javax.annotation.ManagedBean;

import edu.umn.msi.tropix.storage.service.StorageService;

@ManagedBean
public class StorageServiceImpl implements StorageService {

  public String getData(String id) {
    return String.format("Hello World! wit id %s", id);
  }

}
